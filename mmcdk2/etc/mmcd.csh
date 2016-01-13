#! /bin/tcsh -f

# Old directory:
set old_dir = `pwd`

# The shortcut:
set shortcut = $1

# Split shortcut into parts:
echo $shortcut | egrep '[._,]' > /dev/null
if ( $? == 0 ) then
  set parts = `echo $shortcut | tr ._, '   '`
else
  set parts = `echo $shortcut | sed -n 's/\(.\)/\1 /gp'`
endif

# Get checkin root:
if ( $?MM_CHECKIN_ROOT ) then
  set checkin_root = $MM_CHECKIN_ROOT
else
  set checkin_root = $HOME/mumie/checkin
endif

# Get the start path:
if ( $?MMCD_START_PATH ) then
  set start_path = $MMCD_START_PATH
else
  set start_path = content/lineare_algebra
endif

# Find directory, and change into it:
cd $checkin_root/$start_path
set path_abs = `pwd`
set number = ''
foreach part ($parts)
  set number = "${number}${part}_"
  set dir = `ls | egrep "^${number}"`
  set count = `echo $dir | wc -w`
  if ( $count == 0 ) then
    echo "No directory found for shortcut"
    cd $old_dir
    exit 1
  else if ( $count > 1 ) then
    echo "Ambiguous shortcut"
    cd $old_dir
    exit 2
  endif
  cd $dir
  set path_abs = "${path_abs}/${dir}"
end




