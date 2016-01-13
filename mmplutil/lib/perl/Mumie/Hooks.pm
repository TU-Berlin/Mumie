package Mumie::Hooks;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: Hooks.pm,v 1.2 2007/07/11 15:58:55 grudzin Exp $

# The MIT License (MIT)
# 
# Copyright (c) 2010 Technische Universitaet Berlin
# 
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
# 
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
# 
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.

require Exporter;

@ISA = qw(Exporter);

@EXPORT_OK = qw
  (
   run_hook
   append_to_hook
  );


# --------------------------------------------------------------------------------
# The init function
# --------------------------------------------------------------------------------

sub init
  {
    # Nothing to do
    $init_done = 1;
  }

sub reset_init
  {
    $init_done = 0;
  }

# --------------------------------------------------------------------------------
# Hook functions
# --------------------------------------------------------------------------------

sub run_hook
  # ($hook, @args)
  {
    my $hook = shift();
    if ( $hook )
      {
	return(&{$hook}(@_));
      }
    else
      {
	return(0);
      }
  }

sub append_to_hook
  # ($hook, $appended_hook)
  {
    my ($hook, $appended_hook) = @_;
    if ( ( $hook ) && ( $appended_hook ) )
      {
	my $new_hook
	  = sub
              {
		return(&{$hook}(@_), &{$appended_hook}(@_));
	      };
	return($new_hook);
      }
    elsif ( $hook )
      {
	return($hook);
      }
    elsif ( $appended_hook )
      {
	return($appended_hook);
      }
    else
      {
	return(FALSE);
      }
  }

return(1);
