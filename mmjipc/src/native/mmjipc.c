/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2010 Technische Universitaet Berlin
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

#include <stdlib.h>
#include <stdio.h>
#include <errno.h>
#include <string.h>
#include <fcntl.h>
#include <limits.h>
#include <unistd.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <jni.h>
#include "mmjipc.h"

// --------------------------------------------------------------------------------
// Utilities
// --------------------------------------------------------------------------------

// Size of the error message buffer

#define ERRBUF_SIZE 256

// Maximum length of the queue of pending exceptions

#define SOCKET_BACKLOG 5

// Checks if an exception occured (if a pending exception exists); if so, clears the
// exception and throws it.

void checkExc (JNIEnv *env)
{
  jthrowable exc = (*env)->ExceptionOccurred(env);
  if ( exc )
    {
      (*env)->ExceptionClear(env);
      (*env)->Throw(env, exc);
    }
}

// Creates and throws an exception. 'excClsName' is the full qualified class name of
// the exception, 'msg' its message.

void throwExc (JNIEnv *env, char *excClsName, char *msg)
{
  jclass excCls = (*env)->FindClass(env, excClsName);
  if ( excCls == 0 )
    {
      fprintf(stderr, "WARNING: Can not find exception class: %s\n", excClsName);
      fprintf(stderr, "WARNING: %s\n", msg);
      return;
    }
  (*env)->ThrowNew(env, excCls, msg);
}

// Creates a normal string from a jstring and returns it. The returned string is a copy
// of the jstring and does not share any resources with it.

char *jstrToStr (JNIEnv *env, jstring jstr)
{
  const char *buf = (*env)->GetStringUTFChars(env, jstr, 0);
  checkExc(env);
  char *str = (char *)malloc((strlen(buf)+1)*sizeof(char));
  strcpy(str, buf);
  (*env)->ReleaseStringUTFChars(env, jstr, buf);
  checkExc(env);
  return str;
}

// Creates a jstring from a normal string and returns it.

jstring strToJstr (JNIEnv *env, char *str)
{
  jstring jstr = (*env)->NewStringUTF(env, str);
  checkExc(env);
  return jstr;
}

// Returns the field id of a variable of a Java object. 'obj' is the Java object, 'name'
// the name of the variable and 'sign' its signature (specifies its type).

jfieldID getFieldID (JNIEnv *env, jobject obj, char *name, char *sign)
{
  jclass cls = (*env)->GetObjectClass(env, obj);
  checkExc(env);
  jfieldID fid = (*env)->GetFieldID(env, cls, name, sign);
  checkExc(env);
  return fid;
}

// Returns the value of an 'int' variable of a Java object. 'obj' is the Java object,
// 'name' the name of the variable

int getIntField (JNIEnv *env, jobject obj, char *name)
{
  jfieldID fid = getFieldID(env, obj, name, "I");
  int intField = (*env)->GetIntField(env, obj, fid);
  checkExc(env);
  return intField;
}

// Sets the value of an 'int' variable of a Java object.  'obj' is the Java object,
// 'name' the name of the variable, and 'value' the new value of the variable.

void setIntField (JNIEnv *env, jobject obj, char *name, jint value)
{
  jfieldID fid = getFieldID(env, obj, name, "I");
  (*env)->SetIntField(env, obj, fid, value);
  checkExc(env);
} 

// Returns the value of a 'String' variable of a Java object. 'obj' is the Java object,
// 'name' the name of the variable. The returned string is obtained from the corresponding
// jstring by means of 'jstrToStr' (see above).

char *getStrField (JNIEnv *env, jobject obj, char *name)
{
  jfieldID fid = getFieldID(env, obj, name, "Ljava/lang/String;");
  jstring jstr = (*env)->GetObjectField(env, obj, fid);
  checkExc(env);
  return jstrToStr(env, jstr);
}

// Converts a file mode string (something like "rwxr-xr-x") into the corresponding
// numerical mode value and returns the latter.

mode_t strToMode (char *modstr)
{
  int len = strlen(modstr);
  mode_t mode = 0;

  // Bit 0: User read permission:
  if ( len >= 1 && modstr[0] == 'r' ) mode |= S_IRUSR;

  // Bit 1: User write permission:
  if ( len >= 2 && modstr[1] == 'w' ) mode |= S_IWUSR;

  // Bit 2: User execute permission:
  if ( len >= 3 && modstr[2] == 'x' ) mode |= S_IXUSR;

  // Bit 3: Group read permission:
  if ( len >= 4 && modstr[3] == 'r' ) mode |= S_IRGRP;

  // Bit 4: Group write permission:
  if ( len >= 5 && modstr[4] == 'w' ) mode |= S_IWGRP;

  // Bit 5: Group execute permission:
  if ( len >= 6 && modstr[5] == 'x' ) mode |= S_IXGRP;

  // Bit 6: Others read permission:
  if ( len >= 7 && modstr[6] == 'r' ) mode |= S_IROTH;

  // Bit 7: Others write permission:
  if ( len >= 8 && modstr[7] == 'w' ) mode |= S_IWOTH;

  // Bit 8: Others execute permission:
  if ( len >= 9 && modstr[8] == 'x' ) mode |= S_IXOTH;

  return mode;
}

// Converts a numerical mode value into the corresponding string (something like
// "rwxr-xr-x") and returns the latter.

char *modeToStr (mode_t mode)
{
  char *modstr = (char *)malloc(10*sizeof(char));
  if ( modstr == NULL ) return NULL;

  // Bit 0: User read permission:
  if ( mode & S_IRUSR )
    modstr[0] = 'r';
  else
    modstr[0] = '-';

  // Bit 1: User write permission:
  if ( mode & S_IWUSR )
    modstr[1] = 'w';
  else
    modstr[1] = '-';

  // Bit 2: User execute permission:
  if ( mode & S_IXUSR )
    modstr[2] = 'x';
  else
    modstr[2] = '-';

  // Bit 3: Group read permission:
  if ( mode & S_IRGRP )
    modstr[3] = 'r';
  else
    modstr[3] = '-';

  // Bit 4: Group write permission:
  if ( mode & S_IWGRP )
    modstr[4] = 'w';
  else
    modstr[4] = '-';

  // Bit 5: Group write permission:
  if ( mode & S_IXGRP )
    modstr[5] = 'x';
  else
    modstr[5] = '-';

  // Bit 6: Others read permission:
  if ( mode & S_IROTH )
    modstr[6] = 'r';
  else
    modstr[6] = '-';

  // Bit 7: Others write permission:
  if ( mode & S_IWOTH )
    modstr[7] = 'w';
  else
    modstr[7] = '-';

  // Bit 8: Others write permission:
  if ( mode & S_IXOTH )
    modstr[8] = 'x';
  else
    modstr[8] = '-';

  modstr[9] = 0;

  return modstr;
}

// --------------------------------------------------------------------------------
// Native methods of net.mumie.ipc.unix.UnixLib
// --------------------------------------------------------------------------------


JNIEXPORT jint JNICALL Java_net_mumie_ipc_unix_UnixLib_open
  (JNIEnv *env, jclass cls, jstring jfname, jboolean rd, jboolean wr)
{
  const char *fname = (*env)->GetStringUTFChars(env, jfname, 0);
  checkExc(env);
  int flags = (rd && wr ? O_RDWR : (rd ? O_RDONLY : O_WRONLY));
  int fd = open(fname, flags);
  (*env)->ReleaseStringUTFChars(env, jfname, fname);
  checkExc(env);
  return fd;
}

JNIEXPORT jint JNICALL Java_net_mumie_ipc_unix_UnixLib_close
  (JNIEnv *env, jclass cls, jint fd)
{
  return close(fd);
}

JNIEXPORT jint JNICALL Java_net_mumie_ipc_unix_UnixLib_remove
  (JNIEnv *env, jclass cls, jstring jfname)
{
  const char *fname = (*env)->GetStringUTFChars(env, jfname, 0);
  checkExc(env);
  int status = remove(fname);
  (*env)->ReleaseStringUTFChars(env, jfname, fname);
  checkExc(env);
  return status;
}

JNIEXPORT jstring JNICALL Java_net_mumie_ipc_unix_UnixLib_umask
  (JNIEnv *env, jclass cls, jstring jmodstr)
{
  char *modstr = jstrToStr(env, jmodstr);
  mode_t mode = strToMode(modstr);
  mode_t old_mode = umask(mode);
  char *old_modstr = modeToStr(old_mode);
  return (old_modstr == NULL ? NULL : strToJstr(env, old_modstr));
}

JNIEXPORT jint JNICALL Java_net_mumie_ipc_unix_UnixLib_chmod
  (JNIEnv *env, jclass cls, jint fd,
   jboolean u_rd, jboolean u_wr, jboolean u_ex,
   jboolean g_rd, jboolean g_wr, jboolean g_ex,
   jboolean o_rd, jboolean o_wr, jboolean o_ex)
{
  mode_t mode = 0;
  if ( u_rd ) mode |= S_IRUSR;
  if ( u_wr ) mode |= S_IWUSR;
  if ( u_ex ) mode |= S_IXUSR;
  if ( g_rd ) mode |= S_IRGRP;
  if ( g_wr ) mode |= S_IWGRP;
  if ( g_ex ) mode |= S_IXGRP;
  if ( o_rd ) mode |= S_IROTH;
  if ( o_wr ) mode |= S_IWOTH;
  if ( o_ex ) mode |= S_IXOTH;
  return fchmod(fd, mode);
}

JNIEXPORT jint JNICALL Java_net_mumie_ipc_unix_UnixLib_socket
  (JNIEnv *env, jclass cls)
{
  return socket(PF_UNIX, SOCK_STREAM, 0);
}

JNIEXPORT jint JNICALL Java_net_mumie_ipc_unix_UnixLib_bind
  (JNIEnv *env, jclass cls, jint fd, jstring jfname)
{
  const char *fname = (*env)->GetStringUTFChars(env, jfname, 0);
  checkExc(env);
  struct sockaddr_un addr;
  addr.sun_family = AF_UNIX;
  strcpy(addr.sun_path, fname);
  (*env)->ReleaseStringUTFChars(env, jfname, fname);
  checkExc(env);
  size_t addr_len = sizeof(addr.sun_family) + strlen(addr.sun_path);
  return bind(fd, (struct sockaddr *)&addr, addr_len);
}

JNIEXPORT jint JNICALL Java_net_mumie_ipc_unix_UnixLib_listen
  (JNIEnv *env, jclass cls, jint fd)
{
  return listen(fd, SOCKET_BACKLOG);
}

JNIEXPORT jint JNICALL Java_net_mumie_ipc_unix_UnixLib_accept
  (JNIEnv *env, jclass cls, jint fd)
{
  struct sockaddr_un new_addr;
  int new_addr_len = sizeof(new_addr);
  return accept(fd, (struct sockaddr *)&new_addr, &new_addr_len);

  // int new_addr_len = 0;
  // return accept(fd, NULL, &new_addr_len);
}

JNIEXPORT jint JNICALL Java_net_mumie_ipc_unix_UnixLib_connect
  (JNIEnv *env, jclass cls, jint fd, jstring jfname)
{
  const char *fname = (*env)->GetStringUTFChars(env, jfname, 0);
  checkExc(env);
  struct sockaddr_un addr;
  addr.sun_family = AF_UNIX;
  strcpy(addr.sun_path, fname);
  (*env)->ReleaseStringUTFChars(env, jfname, fname);
  checkExc(env);
  size_t addr_len = sizeof(addr.sun_family) + strlen(addr.sun_path);
  return connect(fd, (struct sockaddr *)&addr, addr_len);
}

JNIEXPORT jint JNICALL Java_net_mumie_ipc_unix_UnixLib_read__I_3BII
  (JNIEnv *env, jclass cls, jint fd, jbyteArray jbuf, jint offset, jint len)
{
  jbyte *buf = (*env)->GetByteArrayElements(env, jbuf, 0);
  checkExc(env);
  jbyte *sec = buf+offset;
  int count = read(fd, sec, len);
  (*env)->ReleaseByteArrayElements(env, jbuf, buf, 0);
  checkExc(env);
  return (jint)count;
}

JNIEXPORT jint JNICALL Java_net_mumie_ipc_unix_UnixLib_read__I_3B
  (JNIEnv *env, jclass cls, jint fd, jbyteArray jbuf)
{
  jbyte *buf = (*env)->GetByteArrayElements(env, jbuf, 0);
  checkExc(env);
  jsize len = (*env)->GetArrayLength(env, jbuf);
  checkExc(env);
  int count = read(fd, buf, len);
  (*env)->ReleaseByteArrayElements(env, jbuf, buf, 0);
  checkExc(env);
  return (jint)count;
}

JNIEXPORT jint JNICALL Java_net_mumie_ipc_unix_UnixLib_write__I_3BII
  (JNIEnv *env, jclass cls, jint fd, jbyteArray jbuf, jint offset, jint len)
{
  jbyte *buf = (*env)->GetByteArrayElements(env, jbuf, 0);
  checkExc(env);
  jbyte *sec = buf+offset;
  int count = write(fd, sec, len);
  (*env)->ReleaseByteArrayElements(env, jbuf, buf, 0);
  checkExc(env);
  return (jint)count;
}

JNIEXPORT jint JNICALL Java_net_mumie_ipc_unix_UnixLib_write__I_3B
  (JNIEnv *env, jclass cls, jint fd, jbyteArray jbuf)
{
  jbyte *buf = (*env)->GetByteArrayElements(env, jbuf, 0);
  checkExc(env);
  jsize len = (*env)->GetArrayLength(env, jbuf);
  checkExc(env);
  int count = write(fd, buf, len);
  (*env)->ReleaseByteArrayElements(env, jbuf, buf, 0);
  checkExc(env);
  return (jint)count;
}

JNIEXPORT jstring JNICALL Java_net_mumie_ipc_unix_UnixLib_strerror
  (JNIEnv *env, jclass cls)
{
  char buf[ERRBUF_SIZE];
  char *msg = strerror_r(errno, buf, ERRBUF_SIZE);
  jstring jmsg = (*env)->NewStringUTF(env, msg);
  checkExc(env);
  return jmsg;
}

JNIEXPORT jint JNICALL Java_net_mumie_ipc_unix_UnixLib_kill
  (JNIEnv *env, jclass cls, jint pid, jint sig)
{
  return kill(pid, sig);
}

JNIEXPORT jint JNICALL Java_net_mumie_ipc_unix_UnixLib_SIGINT
  (JNIEnv *env, jclass cls)
{
  return SIGINT;
}

JNIEXPORT jint JNICALL Java_net_mumie_ipc_unix_UnixLib_SIGQUIT
  (JNIEnv *env, jclass cls)
{
  return SIGQUIT;
}

JNIEXPORT jint JNICALL Java_net_mumie_ipc_unix_UnixLib_SIGABRT
  (JNIEnv *env, jclass cls)
{
  return SIGABRT;
}

JNIEXPORT jint JNICALL Java_net_mumie_ipc_unix_UnixLib_SIGKILL
  (JNIEnv *env, jclass cls)
{
  return SIGKILL;
}

JNIEXPORT jint JNICALL Java_net_mumie_ipc_unix_UnixLib_SIGALRM
  (JNIEnv *env, jclass cls)
{
  return SIGALRM;
}

JNIEXPORT jint JNICALL Java_net_mumie_ipc_unix_UnixLib_SIGTERM
  (JNIEnv *env, jclass cls)
{
  return SIGTERM;
}

JNIEXPORT jint JNICALL Java_net_mumie_ipc_unix_UnixLib_SIGCHLD
  (JNIEnv *env, jclass cls)
{
  return SIGCHLD;
}

JNIEXPORT jint JNICALL Java_net_mumie_ipc_unix_UnixLib_SIGCONT
  (JNIEnv *env, jclass cls)
{
  return SIGCONT;
}

JNIEXPORT jint JNICALL Java_net_mumie_ipc_unix_UnixLib_SIGSTOP
  (JNIEnv *env, jclass cls)
{
  return SIGSTOP;
}


