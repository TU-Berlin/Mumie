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
#include <time.h>

void error (char *msg, int exit_val)
{
  fprintf(stderr, "ERROR: %s\n", msg);
  exit(exit_val);
}

int main (int argc, char *argv[])
{
  if ( argc != 2 )
    error("Expecting exactly one argument", 1);

  long int start_time;
  if ( sscanf(argv[1], "%li", &start_time) != 1 )
    error("Failed to read start time", 2);
  time_t end_time = time(NULL);

  long int time = end_time - start_time;
  int secs = time % 60;
  long int mins_raw = (time - secs) / 60;
  int mins = mins_raw % 60;
  long int hours = (mins_raw - mins) / 60;

  if ( time == 0 )
    printf("       <1s");
  else if ( time < 60 )
    printf("       %2is", secs);
  else if ( time < 3600 )
    printf("   %2im %2is", mins, secs);
  else
    printf("%lih %2im %2is", hours, mins, secs);

  return 0;
}
