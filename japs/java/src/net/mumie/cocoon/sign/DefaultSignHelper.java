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

package net.mumie.cocoon.sign;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import net.mumie.cocoon.service.AbstractJapsService;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.LogUtil;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

public class DefaultSignHelper extends AbstractJapsService
  implements Poolable, Configurable, SignHelper
{
  // --------------------------------------------------------------------------------
  // Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(DefaultSignHelper.class);

  /**
   * The default keystore type (<code>"JKS"</code>)
   */

  protected static final String DEFAULT_KEYSTORE_TYPE = "JKS";

  /**
   * The algorithm to create {@link Signature Signature} objects.
   */

  protected static final String ALGORITHM = "SHA1withDSA";

  /**
   * The provider to create {@link Signature Signature} objects.
   */

  protected static final String PROVIDER = "SUN";

  /**
   * Name of the zip entry containing the signed data (<code>"DATA"</code>).
   */

  protected static String DATA = "DATA";

  /**
   * Name of the zip entry containing the signature (<code>"SIGNATURE"</code>).
   */

  protected static String SIGNATURE = "SIGNATURE";

  /**
   * The public key
   */

  protected PublicKey publicKey = null;

  /**
   * The private key
   */

  private PrivateKey privateKey = null;

  // --------------------------------------------------------------------------------
  // Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>DefaultSignHelper</code>.
   */

  public DefaultSignHelper ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Configures this sign helper. See classdecription for details.
   */

  public void configure (Configuration configuration)
    throws ConfigurationException
  {
    final String METHOD_NAME = "configuration";
    this.logDebug(METHOD_NAME + " 1/3: Started");
    try
      {
        String keyStoreFilename =
          configuration.getChild("keystore-filename").getValue().trim();
        String keyStoreType =
          configuration.getChild("keystore-type").getValue(DEFAULT_KEYSTORE_TYPE).trim();
        char[] keyStorePassword =
          configuration.getChild("keystore-password").getValue().trim().toCharArray();
        String keyAlias =
          configuration.getChild("key-alias").getValue().trim();
        char[] keyPassword =
          configuration.getChild("key-password").getValue().trim().toCharArray();
        this.logDebug
          (METHOD_NAME + " 2/3: Done." +
           " keyStoreFilename = " + keyStoreFilename +
           ", keyStoreType = " + keyStoreType +
           ", keyStorePassword = " + LogUtil.hide(keyStorePassword) +
           ", keyAlias = " + keyAlias +
           ", keyPassword = " + LogUtil.hide(keyPassword));
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(new FileInputStream(keyStoreFilename), keyStorePassword);
        if ( !keyStore.isKeyEntry(keyAlias) )
          throw new IllegalArgumentException("No key for alias: " + keyAlias);
        this.privateKey = (PrivateKey)keyStore.getKey(keyAlias, keyPassword);
        this.publicKey = keyStore.getCertificate(keyAlias).getPublicKey();
        this.logDebug(METHOD_NAME + " 3/3: Done");
      }
    catch (Exception exception)
      {
        throw new ConfigurationException("Wrapped exception", exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Signing
  // --------------------------------------------------------------------------------

  /**
   * Creates a signature for the data provided by the specified input stream.
   */

  public byte[] sign (InputStream in)
    throws SignHelperException
  {
    try
      {
        // Initialize the signature object:
        Signature signature = getSignatureInstance();
        signature.initSign(this.privateKey);

        // Supply the signature object the data to be signed:
        updateSignature(signature, in);

        // Return the signature:
        return signature.sign();
      }
    catch (Exception exception)
      {
        throw new SignHelperException(exception);
      }
  }

  /**
   * Creates a signature for certain data and writes the data and the signate to a zip
   * archive. The data is provided by the specified input stream. The zip archive is written
   * to the specified output stream. The archive contains two entries: The data and the
   * signature. The names of the two entries are given by {@link #DATA DATA} and
   * {@link #SIGNATURE SIGNATURE}, respectively.
   *
   * @param in the input strem from which the data to sign are read
   * @param out the output stream the zip archive is written to
   */

  public byte[] signAndZip (InputStream in, OutputStream out)
    throws SignHelperException
  {
    try
      {
        // Initialize the signature object:
        Signature signature = getSignatureInstance();
        signature.initSign(this.privateKey);

        // Initialize the zip archive:
        ZipOutputStream zip = new ZipOutputStream(out);

        // Send the data to the signature object, and store them in the zip:
        zip.putNextEntry(new ZipEntry(DATA));
        byte[] buffer = new byte[1024];
        int length;
        while ( (length = in.read(buffer)) != -1 )
          {
            signature.update(buffer, 0, length);
            zip.write(buffer, 0, length);
          }
        in.close();
        zip.closeEntry();

        // Get the signature:
        byte[] sigBytes = signature.sign();

        // Write the signature to the zip:
        zip.putNextEntry(new ZipEntry(SIGNATURE));
        zip.write(sigBytes);
        zip.closeEntry();

        zip.finish();

        // Return the signature:
        return sigBytes;
      }
    catch (Exception exception)
      {
        throw new SignHelperException(exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Verifying
  // --------------------------------------------------------------------------------

  /**
   * Verifies the data from th specified input stream with respect to the specified
   * signature.
   */

  public boolean verify (byte[] sigBytes, InputStream in)
    throws SignHelperException
  {
    try
      {
        // Initialize the signature object:
        Signature signature = getSignatureInstance();
        signature.initVerify(this.publicKey);

        // Supply the signature object the data to be signed:
        updateSignature(signature, in);

        // Verify the signature:
        return signature.verify(sigBytes);
      }
    catch (Exception exception)
      {
        throw new SignHelperException(exception);
      }
  }

  /**
   * Verifies the data/signature pair stored in the specified zip file. The zip file must
   * have the form of the zip archive described with {@link #signAndZip(in,out) signAndZip}.
   */

  public boolean verifyZip (ZipFile zipFile)
    throws SignHelperException
  {
    try
      {
        ZipEntry dataEntry = null;
        ZipEntry sigEntry = null;

        Enumeration entries = zipFile.entries();
        while ( entries.hasMoreElements() )
          {
            ZipEntry entry = (ZipEntry)entries.nextElement();
            String name = entry.getName();
            if ( name.equals(DATA) && dataEntry == null )
              dataEntry = entry;
            else if ( name.equals(SIGNATURE) && sigEntry == null )
              sigEntry = entry;
            else
              throw new IllegalStateException("Invalid zip entry: " + name);
          }

        if ( sigEntry == null )
          throw new IllegalStateException("Missing signature");

        if ( dataEntry == null )
          throw new IllegalStateException("Missing signed data");

        byte[] sigBytes = readBytes(zipFile.getInputStream(sigEntry));
        InputStream in = zipFile.getInputStream(dataEntry);

        return this.verify(sigBytes, in);
      }
    catch (Exception exception)
      {
        throw new SignHelperException(exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Utilities
  // --------------------------------------------------------------------------------

  /**
   * Returns a string representation of the specified byte array.
   */

  public String bytesToString (byte[] bytes)
  {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < bytes.length; i++)
      {
        int value = bytes[i];
        if ( value < 0 ) value += 256;
        String byteAsString = Integer.toHexString(value).toUpperCase();
        if ( i > 0 )
          buffer.append(":");
        if ( byteAsString.length() < 2 )
          buffer.append("0");
        buffer.append(byteAsString);
      }
    return buffer.toString();
  }

  // --------------------------------------------------------------------------------
  // Internal auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Returns a signature object.
   */

  protected static Signature getSignatureInstance ()
    throws NoSuchAlgorithmException, NoSuchProviderException, IllegalArgumentException 
  {
    return Signature.getInstance(ALGORITHM, PROVIDER);
  }

  /**
   * Updates the specified signature object with the data read from the specified input
   * stream.
   */

  protected static void updateSignature (Signature signature, InputStream in)
    throws IOException, SignatureException 
  {
    byte[] buffer = new byte[1024];
    int length;
    while ( (length = in.read(buffer)) != -1 )
      signature.update(buffer, 0, length);
    in.close();
  }

  /**
   * Reads the contents of the specified output stream and returns them as byte array.
   */

  protected byte[] readBytes (InputStream in)
    throws IOException
  {
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int length;
    while ( (length = in.read(buffer)) != -1 )
      byteStream.write(buffer, 0, length);
    in.close();
    return byteStream.toByteArray();
  }

  // --------------------------------------------------------------------------------
  // Identification method
  // --------------------------------------------------------------------------------
  
  /**
   * Returns a string that identifies this instance. It has the
   * following form:<pre>
   *   "DefaultSignHelper" +
   *   '#' + instanceId
   *   '(' + lifecycleStatus
   *   ')'</pre>
   * where <code>instanceId</code> is the instance id and <code>lifecycleStatus</code> the
   * lifecycle status of this instance.
   */

  public String getIdentification ()
  {
    return
      "DefaultSignHelper" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getLifecycleStatus() +
      ')';
  }
}
