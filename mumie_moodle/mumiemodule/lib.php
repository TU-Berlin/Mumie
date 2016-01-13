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
<?php  // $Id: lib.php,v 1.2 2008/03/28 15:25:39 ritter Exp $
/**
 * Library of functions and constants for module mumiemodule
 *
 * @author PR
 * @version $Id: lib.php
 * @package mumiemodule
 **/
 
 
global $CFG;
require_once $CFG->dirroot.'/mod/mumiemodule/japs_sync/hooks.php'; //for the external synchronisation
require_once $CFG->dirroot.'/mod/mumiemodule/japs_sync/transfer_objects.php';

/**
 * Given an object containing all the necessary data, 
 * (defined by the form in mod_form.php) this function 
 * will create a new instance and return the id number 
 * of the new instance.
 *
 * @param object $mumiemodule - An object from the form in mod.html
 * @return int - The id of the newly inserted newmodule record
 **/
function mumiemodule_add_instance($mumiemodule, $checkfirst=true, $perhand=false) {
    
    global $CFG;
    global $COURSE;
    global $USER;
    
    //check if this is not the firs mumiemodule within this course:
    $morethanone = record_exists('mumiemodule', 'course', $mumiemodule->course);
  
    $mumiemodule->timemodified = time();
    
    	//this is just for later-versions:
		if(!$morethanone){
			$thebigone = create_the_big_one($mumiemodule); //to create the big object containing all data
			insert_all_for_mumie($thebigone);
			} else { //so there are more than one MUMIE-courses related to that class
				//we dont want to have more than one mumiemodule - perhaps later 
				//TODO:
				//del errormessage when adding another mumiemodule-instance
				//rebuild sending of mumie-courses
			}
			
		//insert the new activity into Moodle-DB:
    	$insertedid = insert_record("mumiemodule", $mumiemodule);
    	//and the students
		$context = get_context_instance(CONTEXT_COURSE, $COURSE->id);
		$users = get_users_by_capability($context, 'mod/mumiemodule:participate');
		if ($mumie_groups = groups_get_all_groups($COURSE->id)){
			foreach($mumie_groups as $mumie_group){
				foreach ($users as $user) {
					if(groups_is_member($mumie_group->id, $user->id)){
				    	$ins = new object();
						$ins->mumiemodule = $insertedid;
						$ins->userid  = $user->id;
						$ins->groupid = (int)$mumie_group->id;
						$ins->timemodified = $mumiemodule->timemodified;
						$newid = insert_record("mumiemodule_students", $ins);
				    }
				}
		    }
		}
    if(!$perhand){
    	return $insertedid;
    }else{
    	echo '<h1 align="center">Done!</h1>';
	    print_continue($CFG->wwwroot);
	    admin_externalpage_print_footer();
	    exit;
    }
}

/**
 * Given an object containing all the necessary data, 
 * (defined by the form in mod_form.php) this function 
 * will update an existing instance with new data.
 *
 * @param object $mumiemodule - An object from the form in mod_form.php
 * @return boolean Success/Fail
 **/
function mumiemodule_update_instance($mumiemodule) {

    $mumiemodule->timemodified = time();
    $mumiemodule->id = $mumiemodule->instance;

    return update_record("mumiemodule", $mumiemodule);
    //no need to inform MUMIE - just Moodle-internal changes possible here
}

/**
 * Given an ID of an instance of this module, 
 * this function will permanently delete the instance 
 * and any data that depends on it. 
 *
 * @param int $id - Id of the module instance
 * @return boolean Success/Failure
 **/
function mumiemodule_delete_instance($id) {

    if (! $mumiemodule = get_record("mumiemodule", "id", "$id")) {
        return false;
    }

    $result = true;

    if (! delete_records("mumiemodule", "id", "$mumiemodule->id")) {
        $result = false;
    } else { //delete elements from referenced table
    	if (!delete_records("mumiemodule_students", "mumiemodule", $mumiemodule->id)){
    		$result = false;
    	}
    }

   if($result){
	//TODO:
    //send to MUMIE when sync-order is implemented there
    }

    return $result;
}

/**
 * Return a small object with summary information about what a 
 * user has done with a given particular instance of this module
 * Used for user activity reports.
 * $return->time = the time they did it
 * $return->info = a short text description
 *
 * @return null
 * @todo Finish documenting this function
 **/
function mumiemodule_user_outline($course, $user, $mod, $mumiemodule) {
    //TODO:
    return null;
    
}

/**
 * Print a detailed representation of what a user has done with 
 * a given particular instance of this module, for user activity reports.
 *
 * @return boolean
 * @todo Finish documenting this function
 **/
function mumiemodule_user_complete($course, $user, $mod, $mumiemodule) {
    //TODO:
    return true;
}

/**
 * Given a course and a time, this module should find recent activity 
 * that has occurred in mumiemodule activities and print it out. 
 * Return true if there was output, or false is there was none. 
 *
 * @uses $CFG
 * @return boolean
 * @todo Finish documenting this function
 **/
function mumiemodule_print_recent_activity($course, $isteacher, $timestart) {
    global $CFG;
	//TODO:
    return false;  //  True if anything was printed, otherwise false 
}

/**
 * Function to be run periodically according to the moodle cron
 * This function searches for things that need to be done, such 
 * as sending out mail, toggling flags etc ... 
 *
 * @uses $CFG
 * @return boolean
 * @todo Finish documenting this function
 **/
function mumiemodule_cron () {
    global $CFG;
    return true;
}

/**
 * Must return an array of grades for a given instance of this module, 
 * indexed by user.  It also returns a maximum allowed grade.
 * 
 * Example:
 *    $return->grades = array of grades;
 *    $return->maxgrade = maximum allowed grade;
 *
 *    return $return;
 *
 * @param int $mumiemoduleID of an instance of this module
 * @return mixed Null or object with an array of grades and with the maximum grade
 **/
function mumiemodule_grades($mumiemoduleid) {
   if (!$mumiemodule = get_record('mumiemodule', 'id', $mumiemoduleid)) {
        return NULL;
    }
    if ($mumiemodule->grade == 0) { // No grading
        return NULL;
    }

    $grades = get_records_menu('mumiemodule_students', 'mumiemodule',
                               $mumiemodule>id, '', 'userid, grade');

    $return = new object();

    if ($mumiemodule->grade > 0) {
        if ($grades) {
            foreach ($grades as $userid => $grade) {
                if ($grade == -1) {
                    $grades[$userid] = '-';
                }
            }
        }
        $return->grades = $grades;
        $return->maxgrade = (int)$mumiemodule->grade;

    } else { // Scale
        if ($grades) {
            $scaleid = - ($mumiemodule->grade);
            $maxgrade = "";
            if ($scale = get_record('scale', 'id', $scaleid)) {
                $scalegrades = make_menu_from_list($scale->scale);
                foreach ($grades as $userid => $grade) {
                    if (empty($scalegrades[$grade])) {
                        $grades[$userid] = '-';
                    } else {
                        $grades[$userid] = $scalegrades[$grade];
                    }
                }
                $maxgrade = $scale->name;
            }
        }
        $return->grades = $grades;
        $return->maxgrade = $maxgrade;
    }

    return $return;
}

/**
 * Must return an array of user records (all data) who are participants
 * for a given instance of mumiemodule. Must include every user involved
 * in the instance, independient of his role (student, teacher, admin...)
 * See other modules as example.
 *
 * @param int $mumiemoduleid ID of an instance of this module
 * @return mixed boolean/array of students
 **/
function mumiemodule_get_participants($mumiemoduleid) {
    global $CFG;

/*
    //Get students
    $students = get_records_sql("SELECT DISTINCT u.id, u.id
                                 FROM {$CFG->prefix}user u,
                                      {$CFG->prefix}mumiemodule_students a
                                 WHERE a.mumiemodule = '$mumiemoduleid' and
                                       u.id = a.userid");
    //Get teachers
    $teachers = get_records_sql("SELECT DISTINCT u.id, u.id
                                 FROM {$CFG->prefix}user u,
                                      {$CFG->prefix}mumiemodule_students a
                                 WHERE a.mumiemodule = '$mumiemoduleid' and
                                       u.id = a.teacher");

    //Add teachers to students
    if ($teachers) {
        foreach ($teachers as $teacher) {
            $students[$teacher->id] = $teacher;
        }
    }
    //Return students array (it contains an array of unique users)
    return ($students);
    */
    return false;
}

/**
 * This function returns if a scale is being used by one module
 * it it has support for grading and scales. Commented code should be
 * modified if necessary. See forum, glossary or journal modules
 * as reference.
 *
 * @param int $newmoduleid ID of an instance of this module
 * @return mixed
 * @todo Finish documenting this function
 **/
function mumiemodule_scale_used ($mumiemoduleid,$scaleid) {
    $return = false;

    //$rec = get_record("newmodule","id","$newmoduleid","scale","-$scaleid");
    //
    //if (!empty($rec)  && !empty($scaleid)) {
    //    $return = true;
    //}
   
    return $return;
}

 /**
 * Adds students to the mumiemodule
 * 
 * @param integer $userid - the ID of the user
 * @param integer $mumiemodule - the ID of the mumiemodule
 * @param bool $morethanone - true if there is at least one more mumiemodule-instance within this course
 * @param object $mumie_group - the group in which to subscribe
 */
function mumiemodule_students_subscribe($userid, $mumiemoduleid, $mumie_group, $morethanone = false) {
	global $CFG;
	global $COURSE;
	
    if (record_exists("mumiemodule_students", "mumiemodule", $mumiemoduleid, "userid", $userid, "group", $mumie_group->id)) {
        return true;
    }

	$ins = new object();
	$ins->mumiemodule = $mumiemoduleid;
    $ins->userid  = $userid;
    $ins->groupid = $mumie_group->id;
    $ins->timemodified = time();
    
     //does the MUMIE already know the user?
    $studenttocheck = new object();
    $studenttocheck->syncid = 'moodle-'.$CFG->prefix.'user-'.$userid;
    $userexists = check_student($studenttocheck);
    
    //now we can insert the user into DB
    $newid = insert_record("mumiemodule_students", $ins);
    
    if($newid){
    	if(!$morethanone && !$userexists){ //MUMIE does not know this user yet
	    	$newuser = get_record('user', 'id', $userid);
	    	send_single_user_to_mumie($newuser);
    	}
    	$group_sync_id = 'moodle-'.$CFG->prefix.'groups-'.$mumie_group->id;
		$user_sync_id = 'moodle-'.$CFG->prefix.'user-'.$newuser->id;
		add_user_to_mumie_tutorial($user_sync_id, $group_sync_id);
    }
    return $newid;
}

/**
 * Remove students from a mumiemodule
 * 
 * @param integer $userid
 * @param integer $mumiemoduleid
 * @param integer $groupid
 */
 function mumiemodule_students_unsubscribe($userid, $mumiemoduleid, $groupid){
 	
    if (!record_exists("mumiemodule_students", "mumiemodule", $mumiemoduleid, "userid", $userid, "groupid", $groupid)) {
        return true;
    }
 	
 	return delete_records("mumiemodule_students", "userid", $userid, "mumiemodule", $mumiemoduleid, "groupid", $groupid);
 }

/**
 * This function gets run whenever a role is assigned to a user in a context
 *
 * @param integer $userid
 * @param object $context
 * @return bool
 */
function mumiemodule_role_assign($userid, $context) {
    //here we just want to insert lecturers
    if(has_capability('mod/mumiemodule:teachcourse', $context, $userid)){ 
    	return mumiemodule_add_teacher_subscriptions($userid, $context);
    }
    return true;
}


/**
 * This function gets run whenever a role is assigned to a user in a context
 *
 * @param integer $userid
 * @param object $context
 * @return bool
 */
function mumiemodule_role_unassign($userid, $context) { //TODO: return-werte bearbeiten
	global $CFG;   
    //it is not possible to check if this had been a teacher before removal
    //but we can check if this user was NOT a student in a group in this course
    if($context->contextlevel==CONTEXT_COURSE){
	    if($groups = groups_get_all_groups($context->instanceid, $userid)){
		    foreach($groups as $group){
			    if(groups_is_member($group->id, $userid)){ //so this is a tutor or student
				    $mumiemodules = get_records('mumiemodule', 'course', $context->instanceid); //list of all mumiemodules in the course
				    $mumiemodules = array_values($mumiemodules);
					    if(!record_exists('mumiemodule_students', 'userid', $userid, 'mumiemodule', $mumiemodules[0]->id)){ //so this was no student
							//the user had been a tutor - we have to change the tutorial
							$tutorial = new object();
							$tutorial->syncid = 'moodle-'.$CFG->prefix.'group-'.$group->id;
							$tutorial->tutor = 'moodle-dummy_tutor';
							change_tutorial_for_mumie($tutorial);
						   //the user could have been a tutor but could also been a teacher - so try:
						   return mumiemodule_remove_teacher_subscriptions($userid, $context);
					    } else { //this had been a student that is still in a MUMIE-tutorial
						    $user_sync_id = 'moodle-'.$CFG->prefix.'user-'.$userid;
						    $group_sync_id = 'moodle-'.$CFG->prefix.'group-'.$group->id;
						    remove_user_from_mumie_tutorial($user_sync_id, $group_sync_id);
						    //and now the student has to be removed from all mumiemodules in this course
						    foreach ($mumiemodules as $mumiemodule){
						    	mumiemodule_students_unsubscribe($userid, $mumiemodule, $group->id);
						    }
					    }
			    }
		    }
	    } else { //this user could have been nearly any role - so try:
	    	return mumiemodule_remove_teacher_subscriptions($userid, $context);
	    }
    }
    //for any other context we try to remove teachers:
    return mumiemodule_remove_teacher_subscriptions($userid, $context);
}


/**
 * Recursive function to add subscriptions for new teachers 
 * students are just assigned via groups
 * 
 * @param $userid - the ID of the user added
 * @param $context - the context the user is added in
 * @param roleid - the ID of the user's role
 */
function mumiemodule_add_teacher_subscriptions($userid, $context) {
	global $CFG;
	
    if (empty($context->contextlevel)) {
        return false;
    }

    switch ($context->contextlevel) {

        case CONTEXT_SYSTEM:   // For the whole site 
             if ($courses = get_records('course')) {
                 foreach ($courses as $course) {
                     $subcontext = get_context_instance(CONTEXT_COURSE, $course->id);
                     mumiemodule_add_teacher_subscriptions($userid, $subcontext);
                 }
             }
             break;

        case CONTEXT_COURSECAT:   // For a whole category 
             if ($courses = get_records('course', 'category', $context->instanceid)) {
                 foreach ($courses as $course) {
                     $subcontext = get_context_instance(CONTEXT_COURSE, $course->id);
                     mumiemodule_add_teacher_subscriptions($userid, $subcontext);
                 }
             }
             if ($categories = get_records('course_categories', 'parent', $context->instanceid)) {
                 foreach ($categories as $category) {
                     $subcontext = get_context_instance(CONTEXT_COURSECAT, $category->id);
                     mumiemodule_add_teacher_subscriptions($userid, $subcontext);
                 }
             }
             break;


        case CONTEXT_COURSE:   // For a whole course
             if ($course = get_record('course', 'id', $context->instanceid)) {
                 if ($mumiemodules = get_all_instances_in_course('mumiemodule', $course, $userid, false)) {
                     $fields = 'id, username, password, firstname, lastname';
		             //check if this lecturer is already in mumie as user
		             $lecturertocheck = new object();
		             $lecturertocheck->syncid = 'moodle-'.$CFG->prefix.'user-'.$userid;
		             $lecturerexists = check_lecturer($lecturertocheck, $course->id);
		             if(!$lecturerexists){
		             	$newuser = get_record('user', 'id', $userid, '', '', '', '', $fields);
		                send_single_user_to_mumie($newuser);
		             }
                     foreach ($mumiemodules as $mumiemodule) {
                         if ($modcontext = get_context_instance(CONTEXT_MODULE, $mumiemodule->coursemodule)) {
		                    $course_to_change = new object();
		                    $course_to_change->syncid = 'moodle-'.$CFG->prefix.'course-'.$course->id;
		                    $course_to_change->name = $course->fullname;
		                    $course_to_change->description = $course->summary;
		                    $course_to_change->semester = 'moodle-'.$CFG->prefix.'course_categories-'.$course->category;
		                    $course_to_change->lecturers = '';
							//$context = get_context_instance(CONTEXT_COURSE, $course->id);
							$roleid = get_field('role', 'id', 'shortname', 'editingteacher');
							$fields = 'u.id, u.username, u.password, u.firstname, u.lastname';
							if ($lecturers = get_role_users($roleid, $context, false, $fields)){ 
								foreach($lecturers as $lecturer){
									//get mumie to know the lecturer:
									//send_single_user_to_mumie($lecturer); //MUMIE should already know them all
									$course_to_change->lecturers .= 'moodle-'.$CFG->prefix.'user-'.$lecturer->id.' ';
								}
							}
		                    //$course_to_change->lecturers .= 'moodle-'.$CFG->prefix.'user-'.$userid; //or he is in the list twice
		                    change_class_for_mumie($course_to_change);
                         }
                     }
                 }
             }
             break;

		//is this relevant?? can we really assign a role to just one mumiemodule?????
        case CONTEXT_MODULE:   // Just one module  //necessary for mumiemodule???
             if ($cm = get_coursemodule_from_id('mumiemodule', $context->instanceid)) {
                 if ($mumiemodule = get_record('mumiemodule', 'id', $cm->instance)) {
                     if (has_capability('mod/mumiemodule:tutorize', $modcontext, $userid)){
                             	$newuser = get_record('user', 'id', $userid);
                             	send_single_user_to_mumie($newuser);
                     }
                 }
             }
             break;
    }
    

    return true;
}


/**
 * Recursive function to remove subscriptions for a teacher in a context
 */
function mumiemodule_remove_teacher_subscriptions($userid, $context) {
	global $CFG;
	
    if (empty($context->contextlevel)) {
        return false;
    }

    switch ($context->contextlevel) {

        case CONTEXT_SYSTEM:   // For the whole site
            if ($courses = get_records('course')) {
                foreach ($courses as $course) {
                    $subcontext = get_context_instance(CONTEXT_COURSE, $course->id);
                    mumiemodule_remove_user_subscriptions($userid, $subcontext);
                }
            }
            break;

        case CONTEXT_COURSECAT:   // For a whole category
             if ($courses = get_records('course', 'category', $context->instanceid)) {
                 foreach ($courses as $course) {
                     $subcontext = get_context_instance(CONTEXT_COURSE, $course->id);
                     mumiemodule_remove_user_subscriptions($userid, $subcontext);
                 }
             }
             if ($categories = get_records('course_categories', 'parent', $context->instanceid)) {
                 foreach ($categories as $category) {
                     $subcontext = get_context_instance(CONTEXT_COURSECAT, $category->id);
                     mumiemodule_remove_user_subscriptions($userid, $subcontext);
                 }
             }
             break;

        case CONTEXT_COURSE:   // For a whole course
             if ($course = get_record('course', 'id', $context->instanceid)) {
                 if ($mumiemodules = get_all_instances_in_course('mumiemodule', $course, $userid, true)) {
                     foreach ($mumiemodules as $mumiemodule) {
                         if ($modcontext = get_context_instance(CONTEXT_MODULE, $mumiemodule->coursemodule)) {
                             $course_to_change = new object();
		                     $course_to_change->syncid = 'moodle-'.$CFG->prefix.'course-'.$course->id;
		                     $course_to_change->name = $course->fullname;
		                     $course_to_change->description = $course->summary;
		                     $course_to_change->semester = 'moodle-'.$CFG->prefix.'course_categories-'.$course->category;
		                     $course_to_change->lecturers = '';
							 $context = get_context_instance(CONTEXT_COURSE, $course->id);
							 $fields = 'u.id, u.username, u.password, u.firstname, u.lastname';
							 $roleid = get_field('role', 'id', 'shortname', 'editingteacher');
							 if ($lecturers = get_role_users($roleid, $context, false, $fields)){ 
							 	foreach($lecturers as $lecturer){
									if($userid!=$lecturer->id){
										$course_to_change->lecturers .= 'moodle-'.$CFG->prefix.'user-'.$lecturer->id.' ';
									}
								}
							}
		                    change_class_for_mumie($course_to_change);
                         }
                     }
                 }
             }
             break;

        case CONTEXT_MODULE:   // Just one mumiemodule
             if ($cm = get_coursemodule_from_id('mumiemodule', $context->instanceid)) {
                 if ($mumiemodule = get_record('mumiemodule', 'id', $cm->instance)) {
                     //nix zu tun, da keine teacher im modul stehen
                 }
             }
             break;
    }

    return true;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//					Functions to send the information to HOOKS				///////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/*function send_new_semester_to_mumie(){
		global $CFG;
		global $COURSE;
	    global $USER;
		//zuerst das Semester:
	    $semester = new object();
	    $semester->syncid = 'moodle-'.$CFG->prefix.'course_categories-'.$COURSE->category;
	    $semester->name = get_field('course_categories', 'name', 'id', $COURSE->category); //TODO: Hack durch API ersetzen!!!
	    $semester->description = 'a Moodle-category as semester';
	    insert_semester_for_mumie($semester);
	}*/
	
	/*function send_new_class_to_mumie(){
		global $CFG;
		global $COURSE;
		global $USER;
		//nun die LV:
	    $class = new object();
	    $class->syncid = 'moodle-'.$CFG->prefix.'course-'.$COURSE->id;
	    $class->name = $COURSE->fullname;
	    $class->description = $COURSE->summary;
	    $class->semester = 'moodle-'.$CFG->prefix.'course_categories-'.$COURSE->category;
	    $class->lecturers = '';
	    $context = get_context_instance(CONTEXT_COURSE, $COURSE->id);
	    $fields = 'u.id, u.username, u.password, u.firstname, u.lastname';
	    $roleid = get_field('role', 'id', 'shortname', 'editingteacher');
	    if ($lecturers = get_role_users($roleid, $context, false, $fields)){ 
	    	foreach($lecturers as $lecturer){
	    		//get mumie to know the lecturer:
	    		send_single_user_to_mumie($lecturer);
	    		$class->lecturers .= 'moodle-'.$CFG->prefix.'user-'.$lecturer->id.' ';
	    	}
	    } 
	    insert_class_for_mumie($class);
	}*/
	
	function send_new_tutorial_to_mumie($mumie_group){
	    		global $CFG;
	    		global $COURSE;
	    		global $USER;
	    		$tutorial = new object();
	    		$tutorial->syncid = 'moodle-'.$CFG->prefix.'groups-'.$mumie_group->id;
	    		$tutorial->name = $mumie_group->name;
	    		$tutorial->description = $mumie_group->description;
	    		$tutorial->tutor = 'moodle-dummy_tutor';
	    		$tutorial->classid = 'moodle-'.$CFG->prefix.'course-'.$mumie_group->courseid;
	    		insert_tutorial_for_mumie($tutorial);  
	}
	
	/*
	function send_students_to_mumie(){
		//jetzt die studenten:
	    $context = get_context_instance(CONTEXT_COURSE, $COURSE->id);
	    $fields = 'u.id, u.username, u.password, u.firstname, u.lastname';
	    $mumie_users = get_users_by_capability($context, 'mod/mumiemodule:participate', $fields);
	    if(!empty($mumie_users)){
	    	foreach($mumie_users as $mumie_user){
	    		send_single_user_to_mumie($mumie_user);
	    		
	    		if ($mumie_groups = get_groups($COURSE->id)){
	    			//user zu tutorien hinzufuegen
	    			foreach ($mumie_groups as $mumie_group){
	    				//if(ismember($mummie_group->id, $mumie_user->id)){
	    				if (groups_is_member($mumie_group->id, $mumie_user->id)){
	    					$group_sync_id = 'moodle-'.$CFG->prefix.'course_categories-'.$mumie_group->id;
	    					$user_sync_id = 'moodle-'.$CFG->prefix.'user-'.$mumie_user->id;
	    					add_user_to_mumie_tutorial($user_sync_id, $group_sync_id);
	    				}
	    			}
	    		}
	    	}
	    }
	}
	*/
	
	function send_single_user_to_mumie($mumie_user){
				global $CFG;
	    		global $COURSE;
	    		global $USER;
	    		//user:
				$newuser = new object();
	    		$newuser->syncid = 'moodle-'.$CFG->prefix.'user-'.$mumie_user->id;
	    		$newuser->loginname = $mumie_user->username;
	    		$newuser->passwordencrypted = $mumie_user->password;
	    		$newuser->firstname = $mumie_user->firstname;
	    		$newuser->surname = $mumie_user->lastname;
	    		$newuser->matrnumber = 'X';
	    		insert_user_for_mumie($newuser);
	}


	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////                        New function to create the XML-objects for the interface       /////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	function get_mumie_user($user){
				global $CFG;
				$newuser = new object();
	    		$newuser->syncid = 'moodle-'.$CFG->prefix.'user-'.$user->id;
	    		$newuser->loginname = $user->username;
	    		$newuser->passwordencrypted = $user->password;
	    		$newuser->firstname = $user->firstname;
	    		$newuser->surname = $user->lastname;
	    		$newuser->matrnumber = 'X';
	    		return $newuser;
	}
	
	function create_the_big_one($mumiemodule){
		global $CFG;
		global $USER;
		global $COURSE;
		
		//first the semester
	    $semester_id = 'moodle-'.$CFG->prefix.'course_categories-'.$COURSE->category;
	    $semester_name = get_field('course_categories', 'name', 'id', $COURSE->category); //TODO: replace this hack by api-method when available
	    $semester_description = 'a Moodle-category as semester';
	    $semester = new Semester($semester_id, $semester_name, $semester_description);
	    
	    //lets get the lecturers
	    $context = get_context_instance(CONTEXT_COURSE, $COURSE->id);
	    $fields = 'u.id, u.username, u.password, u.firstname, u.lastname';
	    $lecturers = null;
	    $roleid = get_field('role', 'id', 'shortname', 'editingteacher');
	    if ($mumie_users = get_role_users($roleid, $context, true, $fields)){ 
	    	$lecturers = array();
	    	foreach($mumie_users as $mumie_user){
	    		$newuser = get_mumie_user($mumie_user);
	    		$lecturer = new Lecturer($newuser->syncid, $newuser->loginname, $newuser->passwordencrypted, 
	    								 $newuser->firstname, $newuser->surname, $newuser->matrnumber);
	    		array_push($lecturers, $lecturer);
	    	}
	    } 
	    
	    //now the class
	    $class_id = 'moodle-'.$CFG->prefix.'course-'.$COURSE->id;
	    $class_name = $COURSE->fullname;
	    $class_description = $COURSE->summary;
	     
	    //and now we have the tutorials:
	    $tutorials = null;
	    if($mumie_groups = groups_get_all_groups($COURSE->id)){
			    $tutorials = array();
			    foreach($mumie_groups as $mumie_group){
			    	$tutor = null;
		    		$tutorial_id = 'moodle-'.$CFG->prefix.'groups-'.$mumie_group->id;
		    		$tutorial_name = $mumie_group->name;
		    		$tutorial_description = $mumie_group->description;
		    		//$context = get_context_instance(CONTEXT_GROUP, $mumie_group->id); //no more group_contexts in moodle
		    		$group_users = get_users_by_capability($context, 'mod/mumiemodule:tutorize', $fields, '', '', '', $mumie_group->id);
		    		foreach($group_users as $group_user){
		    			//zur sicherheit, da sonst alle tutoren in jeder gruppe:
			    			if(groups_is_member($mumie_group->id, $group_user->id)){
				    				$newuser = get_mumie_user($group_user);
						    		$tutor = new Tutor($newuser->syncid, $newuser->loginname, $newuser->passwordencrypted, 
						    							$newuser->firstname, $newuser->surname, $newuser->matrnumber);
				    			break; //so just the first tutor will be inserted
			    			} 
		    		}
							//the students within this tutorial:
							$students = null;
							if($group_users = get_users_by_capability($context, 'mod/mumiemodule:participate', $fields)){
								$students = array();
								foreach($group_users as $group_user){
									if(groups_is_member($mumie_group->id, $group_user->id)){
										$newuser = get_mumie_user($group_user);
							    		$student = new Student($newuser->syncid, $newuser->loginname, $newuser->passwordencrypted, 
							    								$newuser->firstname, $newuser->surname, $newuser->matrnumber);
							    		array_push($students, $student);
									}
								}
							}
							//for the transfer_classes:
							$tutorial = new Tutorial($tutorial_id, $tutorial_name, $tutorial_description, $tutor, $students);
							array_push($tutorials, $tutorial);
		    		}
	    }
	    		//don't forget the MUMIE-course
	    		$mumie_course_id = $mumiemodule->mumie_course_id;
	    		$mumie_class = new MumieClass($class_id, $class_name, $class_description, $semester, $lecturers, $tutorials, $mumie_course_id);
		    	
		    	return $mumie_class;
		    
	}
?>
