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
 * Library of handler-functions for module mumiemodule
 *
 * @author PR
 * @version $Id: lib.php,v 1.4 2007/06/22
 * @package mumiemodule
 **/
 
 global $CFG;
 require_once $CFG->dirroot.'/mod/mumiemodule/japs_sync/hooks.php';
 require_once $CFG->dirroot.'/mod/mumiemodule/lib.php';

	/**
	 * Function to handle the user_updated-event
	 * 
	 * @param object $eventdata - the event's data
	 */
	function user_updated_handler($eventdata){
		global $CFG;
		$eventdata = (object)$eventdata;
		//check if the user is relevant for MUMIE
		if(record_exists('mumiemodule_students', 'userid', $eventdata->id)){
			event_logoutput("user_updated_handler called \n", $eventdata);
			$changeduser = new object();
			$changeduser->id = 'moodle-'.$CFG->prefix.'user-'.$eventdata->id;
			$changeduser->loginname = $eventdata->username;
		    $changeduser->passwordencrypted = $eventdata->password;
		    $changeduser->firstname = $eventdata->firstname;
		    $changeduser->surname = $eventdata->lastname;
		    $changeduser->matrnumber = 'X';
		    change_user_for_mumie($changeduser);
		}
		//TODO:
		//check if this might be a tutor that could be relevant for MUMIE
		return true;
	}
	
	/**
	 * Function to handle the password_changed-event
	 * 
	 * @param object $eventdata - the event's data
	 */
	function password_changed_handler($eventdata){
		global $CFG;
		event_logoutput("password_changed_handler called \n", $eventdata);
		//check if the user is relevant for MUMIE
		if(record_exists('mumiemodule_students', 'userid', $eventdata->user->id)){
			$changeduser = get_mumie_user($eventdata->user);
			change_user_for_mumie($changeduser);
		}
		return true;
	}
	
	/**
	 * Function to handle the user_deleted-event
	 * 
	 * @param object $eventdata - the event's data
	 */
	function user_deleted_handler($eventdata){
		global $CFG;
		//check if the user is relevant for MUMIE
		if(record_exists('mumiemodule_students', 'userid', $eventdata->id)){
			event_logoutput("user_deleted_handler called \n", $eventdata);
			//TODO
			//insert functionality when sync-order is implemented in MUMIE
		}
		return true;
	}
	
	/**
	 * Function to handle the course_updated-event
	 * 
	 * @param object $eventdata - the event's data
	 */
	function course_updated_handler($eventdata){
		global $CFG;
		//check if the course is relevant
		if(record_exists('mumiemodule', 'course', $eventdata->id)){
			event_logoutput("course_updated_handler called \n", $eventdata);
			$class = new object();
		    $class->syncid = 'moodle-'.$CFG->prefix.'course-'.$eventdata->id;
		    $class->name = $eventdata->fullname;
		    $class->description = $eventdata->summary;
		    $class->semester = 'moodle-'.$CFG->prefix.'course_categories-'.$eventdata->category;
		    //perhaps the course has been put into another category, so we better check if MUMIE knows
		    $semester = new object();
	    	$semester->syncid = $eventdata->category;
	    	$semester->name = get_field('course_categories', 'name', 'id', $eventdata->category); 
	    	$semester->description = 'a Moodle-category as semester';
		    if(!check_semester($semester)){
		    	$js = new JapsSynchronise();
		    	insert_semester_for_mumie($semester, $js);
		    	change_class_for_mumie($class, $js);
		    } else{
		    	//lecturers can not be changed here
		    	change_class_for_mumie($class);
		    }
		}
		return true;
	}
	
	/**
	 * Function to handle the course_deleted-event
	 * 
	 * @param object $eventdata - the event's data
	 */
	function course_deleted_handler($eventdata){
		global $CFG;
		//check if the course is relevant
		if(strpos($eventdata->modinfo, 'mumiemodule')!==false){ //so there had been at least one mumiemodule in this course
			event_logoutput("course_deleted_handler called \n", $eventdata);
			$classid = 'moodle-'.$CFG->prefix.'course-'.$eventdata->id;
			//TODO
			//delete_class_for_mumie($classid);
		}
		return true;
	}
	
	/**
	 * Function to handle the category_updated-event
	 * 
	 * @param object $eventdata - the event's data
	 */
	function category_updated_handler($eventdata){
		global $CFG;
		global $COURSE;
	    global $USER;
	    //check if this category is a MUMIE-semester
	    $isinteresting = false;
	    $modid = get_field('modules', 'id', 'name', 'mumiemodule');
	    $modcourses = get_records('course_modules', 'module', $modid, '', 'course');
	    foreach($modcourses as $modcourse){
	    	if(record_exists('course', 'id', $modcourse->course, 'category', $eventdata->id)){
	    		$isinteresting = true;
	    		break;
	    	}
	    }
	    if($isinteresting){
		    event_logoutput("category_updated_handler called \n", $eventdata);
		    $semester = new object();
		    $semester->syncid = 'moodle-'.$CFG->prefix.'course_categories-'.$eventdata->id;
		    $semester->name = $eventdata->name;
		    $semester->description = 'a Moodle-category as semester';
		    change_semester_for_mumie($semester);
	    }
		return true;
	}
	
	/**
	 * Function to handle the category_deleted-event
	 * 
	 * @param object $eventdata - the event's data
	 */
	function category_deleted_handler($eventdata){
		global $CFG;
		$isinteresting = false; //we assume that this is not relevant - check!
		//if there is no class with an instance of mumiemodule in the parent category, then we can be sure that this event is not interesting
		//but if there is at least one, we can not be sure - so we better try to send all information
		if($cat_classes = get_courses($semester->parent, 'c.id')){
			foreach($cat_classes as $cat_class){
				if(record_exists('mumiemodule', 'course', $cat_class->id)){
					$isinteresting = true;
					break;
				}
			}
		}
		
	    if($isinteresting){
			event_logoutput("category_deleted_handler called \n", $eventdata);
			$js = new JapsSynchronise();
			$deletedsem = new object();
			$deletedsem->syncid = 'moodle-'.$CFG->prefix.'course_categories-'.$eventdata->id;
			$deletedsem->parent = 'moodle-'.$CFG->prefix.'course_categories-'.$eventdata->parent;
			
			//we try to insert the parent-category as a new semester into MUMIE
			$cat = get_record('course_categories', 'id', $eventdata->parent, '', '', '', '', 'name');
			$newsemester = new object();
			$newsemester->syncid = $deletedsem->parent;
			$newsemester->name = $cat->name;
			$newsemester->description = 'a Moodle-category as semester';
			insert_semester_for_mumie($newsemester, $js);
			
			//the courses were moved from the deleted category into the parent category
			//so we have to change them before deleting the category
			if($cat_classes = get_courses($semester->parent, $fields='c.id')){ //TODO: isn't this deprecated???
			  	foreach($cat_classes as $cat_class){
			  		if(record_exists('mumiemodule', 'course', $cat_class->id)){
			  			$classtochange = new object();
			  			$classtochange->syncid = 'moodle-'.$CFG->prefix.'course-'.$cat_class->id;;
			  			$classtochange->semester = $deletedsem->parent;
			  			change_class_for_mumie($classtochange, $js);
			  		}
		  		}
		  	}
			
			//delete_semester_for_mumie($deletedsem);
	    }
		return true;
	}
	
	/**
	 * Function to handle the group_created-event
	 * 
	 * @param object $eventdata - the event's data
	 */
	function group_created_handler($eventdata){
		global $CFG;
		//check if this group is a MUMIE tutorial
		if(record_exists('mumiemodule', 'course', $eventdata->courseid)){ //later Moodle-Versions check if group fits activity-grouping
			event_logoutput("group_created_handler called \n", $eventdata);
			send_new_tutorial_to_mumie($eventdata);
		}
		
		return true;
	}
	
	/**
	 * Function to handle the group_updated-event
	 * 
	 * @param object $eventdata - the event's data
	 */
	function group_updated_handler($eventdata){
		global $CFG;
		global $USER;
		global $COURSE;
		//check if this group is relevant
		if(record_exists('mumiemodule_students', 'groupid', $eventdata->id)){
			event_logoutput("group_updated_handler called \n", $eventdata);
			$tutorial = new object();
		    $tutorial->id = 'moodle-'.$CFG->prefix.'groups-'.$eventdata->id;
		    $tutorial->name = $eventdata->name;
		    $tutorial->description = $eventdata->description;
		    //tutors can not be changed here
		    $tutorial->classid = 'moodle-'.$CFG->prefix.'course-'.$eventdata->courseid;
		    change_tutorial_for_mumie($tutorial);
		}
		
		return true;
	}
	
	/**
	 * Function to handle the group_user_added-event
	 * 
	 * @param object $eventdata - the event's data
	 */
	function group_user_added_handler($eventdata){
		global $CFG;
		//check if this group is relevant 
		$group = groups_get_group($eventdata->groupid);
		$courseid = $group->courseid;
		//if(record_exists('mumiemodule_students', 'groupid', $eventdata->groupid)){
		//BUT WE SHOULD BETTER CHECK IF THE WHOLE COURSE IS RELEVANT
		if(record_exists('mumiemodule', 'course', $courseid)){
			event_logoutput("group_user_added_handler called \n", $eventdata);
			 //check if current user might be tutor or not
			 //$context = get_context_instance(CONTEXT_GROUP, $eventdata->groupid);
			 $context = get_context_instance(CONTEXT_COURSE, $group->courseid);
			 
			 if(has_capability('mod/mumiemodule:tutorize', $context, $eventdata->userid)){ //this is a tutor
			 	//groups must not have more than one tutor in this activity! //REMOVE AFTER MUMIE-DB CHANGE
			 	$fields = 'u.id';
				//$groupcontext = get_context_instance(CONTEXT_GROUP, $eventdata->groupid);
				$grouptutors = get_users_by_capability($context, 'mod/mumiemodule:tutorize', $fields, '', '', '', $eventdata->groupid);
				if(count($grouptutors)>1){
					$removed = groups_remove_member($eventdata->groupid, $eventdata->userid);
					error(get_string('toomuchtutorssingle', 'mumiemodule'), "../group/members.php?group=".$eventdata->groupid);
					break;
				} //REMOVE TILL HERE	 	
			 	if(!has_capability('mod/mumiemodule:teachcourse', $context, $eventdata->userid)){ //so this is really just a tutor, lecturers are already known in mumie
			 		//perhaps the tutor is already known in MUMIE
			 		$tutortocheck = new object();
			 		$tutortocheck->syncid = 'moodle-'.$CFG.'user-'.$eventdata->userid;
			 		$tutorexists = check_tutor($tutortocheck);
			 		$islecturer = check_lecturer($tutortocheck);
			 		if(!$tutorexists && !$islecturer){
			 			$newuser = get_record('user', 'id', $eventdata->userid);
			 			send_single_user_to_mumie($newuser);
			 		}
			 	}
			 	//start creating the tutorial-object
			 	$tutorial = new object();
			 	$tutorial->syncid = 'moodle-'.$CFG->prefix.'groups-'.$eventdata->groupid;
			 	$tutorial->name = $name->name;
			    $tutorial->description = $group->description;
			    $tutorial->tutor = 'moodle-'.$CFG->prefix.'user-'.$eventdata->userid;
			    $tutorial->classid = 'moodle-'.$CFG->prefix.'course-'.$courseid;
			    change_tutorial_for_mumie($tutorial);
			    
			    
			 } else if(has_capability('mod/mumiemodule:participate', $context, $eventdata->userid)){ //this is a student
			 	 //a student must not be in more than one group in this activity
			 	 $studentsgroups = groups_get_all_groups($courseid, $eventdata->userid);
				 if(count($studentsgroups)>1){
					$removed = groups_remove_member($eventdata->groupid, $eventdata->userid);
					error(get_string('justonegroupsingle', 'mumiemodule'), "../group/members.php?group=".$eventdata->groupid);
					break;
				 }
			 	 
			 	 $mumiemodules = get_records('mumiemodule', 'course', $courseid);
			 	 //variable to keep in mind whether there are more than one mumiemodules in this course
			 	 $morethanone = false;
			 	 foreach($mumiemodules as $mumiemodule){
			 	 	mumiemodule_students_subscribe($eventdata->userid, $mumiemodule->id, $group, $morethanone);
			 	 	$morethanone = true;
			 	 }
			 }
		} else { //ACHTUNG: dies könnte eine nachträglich eingefügte gruppe sein!!!!!!!!!!!!!!!!!!!!!!!!!!
			
		}
		return true;
	}
	
	/**
	 * Function to handle the group_user_removed-event
	 * 
	 * @param object $eventdata - the event's data
	 */
	function group_user_removed_handler($eventdata){
		global $CFG;
		//check if this group is relevant
		$group = groups_get_group($eventdata->groupid);
		$courseid = $group->courseid;
		$context = get_context_instance(CONTEXT_COURSE, $courseid);
		if(record_exists('mumiemodule_students', 'groupid', $eventdata->groupid)){
			event_logoutput("group_user_removed_handler called \n", $eventdata);
			 //check if removed user might be tutor or not
			 if(has_capability('mod/mumiemodule:tutorize', $context, $eventdata->userid)){
			 	//while MUMIE-DB is not changed we insert the Dummy-Tutor:
			 	$tutorial = new object();
			 	$tutorial->syncid = 'moodle-'.$CFG->prefix.'groups-'.$eventdata->groupid;
				$tutorial->name = $eventdata->name;
				$tutorial->description = $group->description;
				$tutorial->tutor = 'moodle-dummy_tutor';
				$tutorial->classid = 'moodle-'.$CFG->prefix.'course-'.$courseid;
				change_tutorial_for_mumie($tutorial);
			 } else if(has_capability('mod/mumiemodule:participate', $context, $eventdata->userid)){ //this is a student
				$userid = 'moodle-'.$CFG->prefix.'user-'.$eventdata->userid;
				$tutorialid = 'moodle-'.$CFG->prefix.'groups-'.$eventdata->groupid;
				remove_user_from_mumie_tutorial($userid, $tutorialid);
				//a user must be part in exactly one tutorial per course - so the user has to be removed from the modules of this course
				$mumiemodules = get_records('mumiemodule', 'course', $courseid);
				foreach($mumiemodules as $mumiemodule){
					mumiemodule_students_unsubscribe($eventdata->userid, $mumiemodule->id, $eventdata->groupid);
				}
			 }
		}
		return true;
	}
	
	/**
	 * Function to handle the group_deleted-event
	 * 
	 * @param object $eventdata - the event's data
	 */
	function group_deleted_handler($eventdata){
		global $CFG;
		global $COURSE;
		//check if this group is relevant
		if(record_exists('mumiemodule_students', 'groupid', $eventdata->group)){
			event_logoutput("group_deleted_handler called \n", $eventdata);
			 $tutorialid = 'moodle-'.$CFG->prefix.'groups-'.$eventdata->group;
			 
			 //if there had been students in that group, remove
			 //THIS PART OF CODE SHOULD BE REMOVED WHEN THE DELETE-SYNC-ORDER IS IMPLEMENTED IN MUMIE
			 //MUMIE ITSELF SHOULD THEN HANDLE THOSE TUTORIAL-INTERNAL REMOVEMENTS
			 $groupusers = get_records('mumiemodule_students', 'group', $eventdata->group, '', 'userid');
			 $js = new JapsSynchronise();
			 $fields = 'id, username, password, firstname, lastname';
			 foreach($groupusers as $groupuser){
			 	$userid = 'moodle-'.$CFG->prefix.'user-'.$groupuser->id;
			 	remove_user_from_mumie_tutorial($userid, $tutorialid, $js);
			 }//END OF CODE TO REMOVE
			 
			//delete_tutorial_for_mumie($tutorialid); //TODO
		}
		return true;
	}
	
	/**
 * Function to write some information into a logfile
 */
function event_logoutput($event_name, $eventdata){
	global $CFG;
	if($LOGG = fopen($CFG->dirroot.'/mod/mumiemodule/logs/event_log.txt', 'a')){
		fputs($LOGG, date("F d Y h:i:s A").": ");
		fputs($LOGG, $event_name);
		fputs($LOGG, dump_array($eventdata));
		fputs($LOGG, "\n \n");
	}
}
?>
