; ----------------------------------------------------------------------------------------------
; Please use as command line like this:
;
; xemacs -l ~/mmtex/utils/htmlize_whole_directory.el -eval '(htmlize-tree("~/mmtex/")'
;
; ----------------------------------------------------------------------------------------------


(setq debug-on-error "t")

(defun htmlize-tree (command-line-rootdir)
"Short command to start it from the shell"

(setq root_dir (replace-tilde command-line-rootdir))

; Please define the types of files you want to highlight and 
; specify a directory where the resulting highlighted source
; code should be placed..


(setq source-doc-targets
(list  (list "PERL" (concat root_dir "/docs/source_docs/perl/"))
       (list "XML"  (concat root_dir "/docs/source_docs/xml/"))
       (list "CSS"  (concat root_dir "/docs/source_docs/css/"))))

(setq highlight-filetypes-extensions
(list (list "PERL" (list "pl" "pm"))))
;      (list "XML"  (list "xml" "xsl"))
;      (list "CSS"  (list "css"))))
(directory-files-recursive root_dir 'include-file-htmlize)      

(kill-emacs)
)








(defun include-file-htmlize (file_name)
  "This function check every file for the extensions given in 'highlighted-filetypes-extensions'
and if the extension matches, the according folder in 'source-doc-targets' is choosen as destination
for the htmlize-file function"

(let
((file-types highlight-filetypes-extensions))

(while (first (first file-types))

  (setq current-type (first (first file-types)))
  (setq current-extensions (cdr-safe (assoc current-type file-types)))
  (setq current-extensions (first current-extensions))

  (while (first current-extensions)
  
    (if (equal (first current-extensions) (file-name-extension file_name))

      (if (cdr-safe (assoc current-type source-doc-targets))
	  (htmlize-files-dirtree-copy file_name root_dir (car-safe (cdr-safe (assoc current-type source-doc-targets))))
	(error (concat "You haven't set a path for highlighted this type of files: " current-type)))
    )
    (setq current-extensions (cdr-safe current-extensions))
  )
  
  (setq file-types (cdr-safe file-types))
)))





(defun replace-tilde (dirname)
  "Replaces the tilde ~ in front of a directory string with the home directory"
  
(let
((homedir (file-name-directory (first (directory-files "~" "full-name")))))

; Check for the beginning syntax and replace it..
(if (string-starts-with-p dirname "~/")
      (concat homedir (substring dirname 2))

  (if (string-starts-with-p dirname "~")
      (concat homedir (substring dirname 1))

    dirname))

))






(defun htmlize-files-dirtree-copy (file_name root_dir target_dir)
  "Htmlize a file and place the copy in the target_dir creating a corresponding
directory tree assuring all subdirs under root_dir will be created in target_dir."

; root_dir    => the basic directory used to create the same tree in the target dir
; target_dir  => may be anywhere, all files in directories under root_dir will be 
;                highlighted and stored in the corresponding directory under target_dir
;
; present_dir => The directory of the present file


(setq present_dir (file-name-directory file_name))

; Check that present_dir is child of root_dir
(if (not (string-match (concat "^" root_dir) present_dir))
    (progn 
      (message root_dir)
      (sit-for 2)
      (message present_dir)
      (sit-for 2)
      (error "Error: Parsing directory have to be subdirectory of root_dir"))
    (progn

       ; Removing the root_dir and creating the full target_dir
      (setq target_subdir (substring present_dir (length root_dir)))
      (setq target_wholedir (concat target_dir target_subdir))
      (if (not (file-exists-p target_wholedir))
	  (make-directory target_wholedir "create_Parents"))

      (htmlize-file file_name target_wholedir)
    )
))









;  -------- Written by Christian Ruppert
;
;  Provides an extension to the function directory-files named directory-files-recursive
;  Enables a recursive run through a given directory and the option to pass two 
;  functions to handle each occuring dir or file entry
;
;  See documentation and example handle-function (directory-files-recursive-display-entry)



(defun directory-files-recursive (dirname filefunc &optional dirfunc)

  "Recursive search through a directory. 

The function filefunc is applied on every file found in the current directory.

The function dirfunc is applied on every subdirectory of the current directory.
If the result of the dirfunc is NIL, the subdirectory WILL be parsed, if it's 
anything else (e.g \"skip\") the subdirectory will be ignored.

If no dirfunc is given, all subdirectories will be parsed.

Please take a look at the example function 'directory-files-recursive-display-entry'
to see an example of a filefunc and a dirfunc. (This example can handle both)"


(if (not (string-match  "/.$" dirname)) 
(if (not (string-match "/..$" dirname))


(let
((file-list (directory-files (file-name-as-directory dirname) 'full-name)))

(while file-list

  (if (file-directory-p (car file-list))
  
      ;Handle all subdirectories
      (if dirfunc
	    (if (not (funcall dirfunc (car file-list)))
	        (directory-files-recursive (car file-list) filefunc dirfunc))
            (directory-files-recursive (car file-list) filefunc)
      )

      ; Handle all files
      (funcall filefunc (car file-list))
  )
  (setq file-list (cdr file-list))

)))))






(defun directory-files-recursive-display-entry (name)
  "Example help function for directory-files-recursive...
Will create a buffer named 'directory-files-recursive-display-entry' and show all file
names and all directories with some kind of easy indent markup.

Please take a look at the source code to learn more about writing own functions
for working with 'directory-files-recursive' and skipping directories be returning not nil

To test it, please type somewhere in a buffer: (I know, it's a little long)

(directory-files-recursive \"~/directory\" 'directory-files-recursive-display-entry 'directory-files-recursive-display-entry)"

(switch-to-buffer (get-buffer-create "directory-files-recursive-display-entry"))

;Indent Level (three times the directory count)
(insert (make-string (* (safe-length (split-string name "/")) 3) ? ))

;Display directory or file entry
(if (file-directory-p name)
    (progn
      (insert "(+++) ")
      (insert (file-name-nondirectory name)))

    (insert (file-name-nondirectory name)))

(insert "\n")

; Possible way to exclude directories from recursive parsing by returning not nil
; Please remember that they are still listed, (insert above does not test first) and
; that '.' and '..' are ALWAYS skipped (otherwise recursive would not be a good idea)
(if (file-directory-p name)
    (progn
     (if (string-match (file-name-nondirectory name) "CVS") "skip")
;    (if (string-match (file-name-nondirectory name) "TRASH")   "skip")
;    (if (string-match (file-name-nondirectory name) "ATTIC")   "skip")
;    ... and so on ...
    )
))
 





