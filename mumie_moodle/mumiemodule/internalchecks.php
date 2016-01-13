<?php

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

?>
<?php
/**
 *
 * script with functions for some internal checks
 * so not everythins has to be send to MUMIE
 */
 
 /**
  * Function to check if a semester is already known in the MUMIE
  * 
  * @param object $semester - the semester to be checked
  */
  function check_semester($semester){
    global $CFG;
  	$delimiter = 'moodle-'.$CFG->prefix.'course_categories-';
  	$sync_id_parts = explode($delimiter, $semester->syncid);
  	$sort = 'c.sortorder ASC';
  	$cat_classes = get_courses($sync_id_parts[1], $sort, $fields='c.id'); //TODO: isn't this deprecated???
  	foreach($cat_classes as $cat_class){
  		if(record_exists('mumiemodule', 'course', $cat_class->id)){
  			return true;
  			break;
  		}
  	}
  	return false;
  }
 
 /**
   * Function to check if a lecturer is already known in the MUMIE
   * 
   * @param object $lecturer - the lecturer to be checked
   * @param integer $currentcourseid - the ID of the current course (because there the lecturer is definitely in)
   * 
   */
   function check_lecturer($lecturer, $currentcourseid=-1){
   		global $CFG;
   		$lecturer_delimiter = 'moodle-'.$CFG->prefix.'user-';
		$sync_id_parts = explode($lecturer_delimiter, $lecturer->syncid);
		$classes_in_mumie = get_records('mumiemodule', '', '', '', 'course');
		foreach($classes_in_mumie as $class_in_mumie){
			$coursecontext = get_context_instance(CONTEXT_COURSE, $class_in_mumie->course);
			///////////////////////// Followin is just for the server-test ////////////////////////////////////////
			$sitecontext = get_context_instance(CONTEXT_SYSTEM, SITEID);
			$admin = false;
			if(has_capability('moodle/course:create', $sitecontext, $sync_id_parts[1])){
				$admin = true;
			}/////////////////////////////////////////////////////////////////////////////////////////////////////
			if(has_capability('mod/mumiemodule:teachcourse', $coursecontext, $sync_id_parts[1]) && $class_in_mumie->course!=$currentcourseid && !$admin){
				return true;
			}
		}
		return false;
   }
   
   /**
    * Function to check if a tutor is already known in MUMIE
    * 
    * @param object $tutor - the tutor to be checked
    * 
    */
    function check_tutor($tutor){
    	global $CFG;
    	$delimiter = 'moodle-'.$CFG->prefix.'user-';
		$sync_id_parts = explode($delimiter, $tutor->syncid);
		$fields = 'u.id';
		$classes_in_mumie = get_records('mumiemodule', '', '', '', 'course');
		//$context = get_context_instance(CONTEXT_COURSE, );
		foreach($classes_in_mumie as $class_in_mumie){
			$context = get_context_instance(CONTEXT_COURSE, $class_in_mumie->course);
			$groups_in_class = groups_get_all_groups($class_in_mumie->course);
			foreach($groups_in_class as $group_in_class){
				//$groupcontext = get_context_instance(CONTEXT_GROUP, $group_in_class);
				//////////////////////////////////// Following is just for server-test ///////////////////////////////////////////
				$sitecontext = get_context_instance(CONTEXT_SYSTEM, SITEID);
				$admin = has_capability('moodle/course:create', $sitecontext, $sync_id_parts[1]);
				/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				if(has_capability('mod/mumiemodule:tutorize', $context, $sync_id_parts[1]) && !$admin && groups_is_member($group_in_class, $sync_id_parts[1])){
					return true;
					break;
				}
			}
		}
		return false;
    }
    
    /**
     * Function to check if a student is already known in MUMIE
     * 
     * @param object $student - the student to be checked
     */
     function check_student($student){
     	global $CFG;
     	$delimiter = 'moodle-'.$CFG->prefix.'user-';
		$sync_id_parts = explode($delimiter, $student->syncid);
		if(record_exists('mumiemodule_students', 'userid', $sync_id_parts[1])){
			return true;
		}
		return false;
     }
 
 
?>
