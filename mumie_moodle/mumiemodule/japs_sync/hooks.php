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
 * Library of functions to send information to MUMIE 
 * for module mumiemodule
 *
 * @author PR
 * @package mumiemodule
 **/
 
require_once 'HttpURLConnection.class.php';
require_once 'JapsClient.class.php';
require_once 'JapsSynchronise.class.php';
 
 global $CFG;
 global $COURSE;
 global $USER;
 require_once $CFG->dirroot.'/mod/mumiemodule/lib.php';
 require_once $CFG->dirroot.'/mod/mumiemodule/internalchecks.php';
 
 /**
  * Function to insert all neccesarry information out of a new MUMIE-module into MUMIE
  * 
  * @param object $insert - big object with all information
  */
  function insert_all_for_mumie($insert){
    global $CFG;
    global $COURSE;
  	$js = new JapsSynchronise();	
  	//first we insert the new semester if MUMIE does not know it yet
  	$semesterexists = check_semester($insert->semester);
  	if(!$semesterexists){
  		insert_semester_for_mumie($insert->semester, $js);
  	}
  	//now we insert the new class
  	$newclass = new object();
  	$newclass->syncid = $insert->syncid;
	$newclass->name = $insert->name;
	$newclass->description = $insert->description;
	$newclass->semester = $insert->semester->syncid;
	$newclass->lecturers = '';
	//let's be sure to have all lecturers as users in the MUMIE DB
	if($insert->lecturers){
		foreach($insert->lecturers as $lecturer){
			//perhaps the lecturer is already in the MUMIE
			$lecturerexists = check_lecturer($lecturer);
			if(!$lecturerexists){
				insert_user_for_mumie($lecturer, $js);
			}
			$newclass->lecturers .= $lecturer->syncid.' ';
		}
	}
	$newclass->mumie_course_id = $insert->mumie_course_id;
	insert_class_for_mumie($newclass, $js);
	//next is the tutorials
	if($insert->tutorials){
		foreach($insert->tutorials as $tutorial){
			//we should make sure that the tutor is a user in the Mumie DB
			if($tutorial->tutor){
				//perhaps the tutor is already in the MUMIE
				$tutorexists = check_tutor($tutorial->tutor);
				$tutor_is_lecturer = check_lecturer($tutorial->tutor);
				if(!$tutorexists && !$tutor_is_lecturer){ 
					insert_user_for_mumie($tutorial->tutor, $js);
				}
				$tutorial->tutor = $tutorial->tutor->syncid;
			}
			else { //no tutor => insert dummy
				$tutorial->tutor = 'moodle-dummy_tutor';
			}
			$tutorial->classid = $insert->syncid;
			insert_tutorial_for_mumie($tutorial, $js);
			if($tutorial->students){
				foreach($tutorial->students as $student){
					//perhaps MUMIE already knows this student so we first check
					$studentexists = check_student($student);
					if(!$studentexists){
						insert_user_for_mumie($student, $js);
					}
					add_user_to_mumie_tutorial($student->syncid, $tutorial->syncid, $js);
				}
			}
		}
	}
  }
  
  
 /**
  * Function to insert a Moodle-course_category into MUMIE as a semester
  * 
  * @param $semester - object with specific information
  */
 function insert_semester_for_mumie($semester, $js=null){
 	$syncID = $semester->syncid;
 	$syncData = array("sync-id"=>$syncID, "name"=>$semester->name, "description"=>$semester->description);
 	//send new semester info to MUMIE
 	$sync_order = 'new-semester';
 	if(!$js){
 		$js = new JapsSynchronise();
 	}
 	// adding "protected/sync/" before the synchronisations type specific path
    $cocoonPath = "protected/sync/".$sync_order;
    // Starting the syncronisation and getting the result
    $response = $js->post($cocoonPath, $syncData);
    $filteredResponse = filter_response($response);
    logoutput($filteredResponse, $sync_order, $syncData);
    // Take a look if the response is okay
    if($filteredResponse != "OK"){
     	//events API should care about errors
     	//so no error-handling here - perhaps later
     }
 }
 
 /**
  * Function to change semester-information in MUMIE
  * 
  * @param $semester - object containing the new information
  */
 function change_semester_for_mumie($semester, $js=null){
 	$syncID = $semester->syncid;
 	$syncData = array("sync-id"=>$syncID, "name"=>$semester->name, "description"=>$semester->description);
 	$sync_order = 'change-semester-data';
 	if(!$js){
	 	$js = new JapsSynchronise();
 	}
 	// adding "protected/sync/" before the synchronisations type specific path
    $cocoonPath = "protected/sync/".$sync_order;
    // Starting the syncronisation and getting the result
    $response = $js->post($cocoonPath, $syncData);
    $filteredResponse = filter_response($response);
    logoutput($filteredResponse, $sync_order, $syncData);
    // Take a look if the response is okay
    if($filteredResponse != "OK"){
     	//space for error-handling if we need some later
     }
 }
 
 /**
  * Function to delete a semester in MUMIE and eventually change it's classes to a new one
  * 
  * @param object $semester - object containing the information about deleted and parent semester
  */
  function delete_semester_for_mumie($semester, $js=null){
  	global $CFG;
  	if(!$js){
  		$js = new JapsSynchronise();
  	}
  	//sync-order not implemented yet in MUMIE //TODO
  }
 
 /**
  * Function to insert a Moodle-course into MUMIE as a new MUMIE-class
  * 
  * @param $class - object containing the new class-information
  */
 function insert_class_for_mumie($class, $js=null){
 	$syncID = $class->syncid;
 	$syncData = array("sync-id"=>$syncID, "name"=>$class->name,"description"=>$class->description, 
					  "semester"=>$class->semester, "lecturers"=>$class->lecturers, "courses"=>$class->mumie_course_id);
 	//send new class to MUMIE
 	$sync_order = 'new-class';
 	if(!$js){
	 	$js = new JapsSynchronise();
 	}
 	// adding "protected/sync/" before the synchronisations type specific path
    $cocoonPath = "protected/sync/".$sync_order;
    // Starting the syncronisation and getting the result
    $response = $js->post($cocoonPath, $syncData);
    $filteredResponse = filter_response($response);
    logoutput($filteredResponse, $sync_order, $syncData);
    // Take a look if the response is okay
    if($filteredResponse != "OK"){
     	//MUMIE error
     }
 }
 
 /**
  * Function to change class-information in MUMIE
  * 
  * @param $class - object containing the changed information
  */
 function change_class_for_mumie($class, $js=null){
 	$syncID = $class->syncid;
 	$syncData = array("sync-id"=>$syncID);
 	if($class->name){
 		$syncData["name"]=$class->name;
 	}
 	if($class->description){
 		$syncData["description"]=$class->description;
 	}
 	if($class->semester){
 		$syncData["semester"]=$class->semester;
 	}
 	if($class->lecturers){
 		$syncData["lecturers"]=$class->lecturers;
 	}
 	//no change of MUMIE-course possible, so we discard
 	$sync_order = 'change-class-data';
 	if(!$js){
 		$js = new JapsSynchronise();
 	}
 	// adding "protected/sync/" before the synchronisations type specific path
    $cocoonPath = "protected/sync/".$sync_order;
    // Starting the syncronisation and getting the result
    $response = $js->post($cocoonPath, $syncData);
    $filteredResponse = filter_response($response);
    logoutput($filteredResponse, $sync_order, $syncData);
    // Take a look if the response is okay
    if($filteredResponse != "OK"){
     	//for later error-handling
     }
 }
 
 /**
  * Function to insert a Moodle-group into MUMIE as a new tutorial
  * 
  * @param $tutorial - object containing the necessary information
  */
 function insert_tutorial_for_mumie($tutorial, $js=null){
 	$syncID = $tutorial->syncid;
 	$syncData = array("sync-id"=>$syncID, "name"=>$tutorial->name, "description"=>$tutorial->description, 
					  "tutor"=>$tutorial->tutor, "class"=>$tutorial->classid);
 	$sync_order = 'new-tutorial';
 	if(!$js){
 		$js = new JapsSynchronise();
 	}
 	// adding "protected/sync/" before the synchronisations type specific path
    $cocoonPath = "protected/sync/".$sync_order;
    // Starting the syncronisation and getting the result
    $response = $js->post($cocoonPath, $syncData);
    $filteredResponse = filter_response($response);
    logoutput($filteredResponse, $sync_order, $syncData);
    // Take a look if the response is okay
    if($filteredResponse != "OK"){
     	//perhaps the tutorial already exists and we just want to update
     	//change_tutorial_for_mumie($tutorial);
     }
 }
 
 /**
  * Function to change tutorial-information in MUMIE
  * 
  * @param $tutorial - object containing the changed information
  */
 function change_tutorial_for_mumie($tutorial, $js=null){
 	$syncID = $tutorial->syncid;
 	$syncData = array("sync-id"=>$syncID);
 	if($tutorial->name){
 		$syncData["name"]=$tutorial->name;
 	}
 	if($tutorial->description){
 		$syncData["description"]=$tutorial->description;
 	}
 	//if($tutorial->tutor){
 		$syncData["tutor"]=$tutorial->tutor;
 	//}
 	if($tutorial->classid){
 		$syncData["class"]=$tutorial->classid;
 	}
 	$sync_order = 'change-tutorial-data';
 	if(!$js){
 		$js = new JapsSynchronise();
 	}
 	// adding "protected/sync/" before the synchronisations type specific path
    $cocoonPath = "protected/sync/".$sync_order;
    // Starting the syncronisation and getting the result
    $response = $js->post($cocoonPath, $syncData);
    $filteredResponse = filter_response($response);
    logoutput($filteredResponse, $sync_order, $syncData);
    // Take a look if the response is okay
    if($filteredResponse != "OK"){
     	//for later error-handling
     }
 }
 
 /**
  * Function to insert a new user into MUMIE
  * 
  * @param $user - object containing the information about the new user
  */
 	function insert_user_for_mumie($newuser, $js=null){
     	//the Moodle_to_MUMIE_login_workaround:
     	$newuser->passwordencrypted = hash_internal_user_password($newuser->passwordencrypted);
     	$syncID = $newuser->syncid;
     	$syncData = array("sync-id"=>$syncID,"login-name"=>$newuser->loginname,"password-encrypted"=>$newuser->passwordencrypted,"first-name"=>$newuser->firstname,"surname"=>$newuser->surname, "matr-number"=>$newuser->matrnumber);
     	$sync_order = 'new-user';
     	if(!$js){
 			$js = new JapsSynchronise();
     	}
 		// adding "protected/sync/" before the synchronisations type specific path
    	$cocoonPath = "protected/sync/".$sync_order;
    	// Starting the syncronisation and getting the result
    	$response = $js->post($cocoonPath, $syncData);
    	$filteredResponse = filter_response($response);
    	logoutput($filteredResponse, $sync_order, $syncData);
    	// Take a look if the response is okay
    	if($filteredResponse != "OK"){
     		//for later error-handling
     	}    	
     }
     
     /**
      * Function to change user-information in MUMIE+
      * 
      * @param $newUser - object with the changed user-data
      */
     function change_user_for_mumie($newuser, $js=null){
     	//Moodle to MUMIE login_workaround:
     	$newuser->passwordencrypted = hash_internal_user_password($newuser->passwordencrypted);
     	$syncID = $newuser->id;
     	$syncData = array("sync-id"=>$syncID,"login-name"=>$newuser->loginname,"password-encrypted"=>$newuser->passwordencrypted,"first-name"=>$newuser->firstname,"surname"=>$newuser->surname, "matr-number"=>$newuser->matrnumber);
     	$sync_order = 'change-user-data';
     	if(!$js){
 			$js = new JapsSynchronise();
     	}
 		// adding "protected/sync/" before the synchronisations type specific path
    	$cocoonPath = "protected/sync/".$sync_order;
    	// Starting the syncronisation and getting the result
    	$response = $js->post($cocoonPath, $syncData);
    	$filteredResponse = filter_response($response);
    	logoutput($filteredResponse, $sync_order, $syncData);
    	// Take a look if the response is okay
    	if($filteredResponse != "OK"){
     		//for later error-handling
     	}    	
     }
 
 /**
  * Function to add a Moodle-group-user into a MUMIE-tutorial
  * 
  * @param $newuserID - string containing the Sync-ID of the user for MUMIE
  * @param $syncID - string containing the Sync-ID of the tutorial for MUMIE
  */
 	function add_user_to_mumie_tutorial($newuserID, $syncID, $js=null){
 		$syncData = array("tutorial"=>$syncID, "user"=>$newuserID);
 		$sync_order = 'add-user-to-tutorial';
 		if(!$js){
 			$js = new JapsSynchronise();
 		}
 		// adding "protected/sync/" before the synchronisations type specific path
    	$cocoonPath = "protected/sync/".$sync_order;
    	// Starting the syncronisation and getting the result
    	$response = $js->post($cocoonPath, $syncData);
    	$filteredResponse = filter_response($response);
    	logoutput($filteredResponse, $sync_order, $syncData);
    	// Take a look if the response is okay
    	if($filteredResponse != "OK"){
     		//error-handling
     	}  
 	}
 	
 /**
  * Function to remove a Moodle-group-user from a MUMIE-tutorial
  * 
  * @param $newuserID - string containing the Sync-ID of the user for MUMIE
  * @param $syncID - string containing the Sync-ID of the tutorial for MUMIE
  */
 	function remove_user_from_mumie_tutorial($userID, $syncID, $js=null){
 		$syncData = array("tutorial"=>$syncID, "user"=>$userID);
 		$sync_order = 'remove-user-from-tutorial';
 		if(!$js){
 			$js = new JapsSynchronise();
 		}
 		// adding "protected/sync/" before the synchronisations type specific path
    	$cocoonPath = "protected/sync/".$sync_order;
    	// Starting the syncronisation and getting the result
    	$response = $js->post($cocoonPath, $syncData);
    	$filteredResponse = filter_response($response);
    	logoutput($filteredResponse, $sync_order, $syncData);
    	// Take a look if the response is okay
    	if($filteredResponse != "OK"){
     		//for error-handling later
     	}  
 	}
 

/*************************************************************************************
 * 							Getter Functions
 *************************************************************************************/
 
 /**
  * Function to get a list of all courses in MUMIE
  * 
  * @param $js - instance of class JapsSynchronise (optional)
  */
 function get_mumie_courses($js = null){
 	//$get_order = 'get-courses';
 	if(!$js){
 		$js = new JapsSynchronise();
 	}
    $cocoonPath = "protected/data/document-index/type-name/course";
    // Starting the request and getting the result
    $response = $js->get($cocoonPath);
    $filteredResponse = filter_response($response);
    $course_array = filter_response_to_array($filteredResponse, 'courses');
    
    //if we do not want to also have the courses that are already in use elsewhere, we put that code back in
    /*
    $return_array = array();
    foreach($course_array as $course_item){
    	if($course_item->alreadyused === false){
 			array_push($return_array, $course_item);
    	}
    }
    return $return_array;
    */
    return $course_array;
 }
 
 /**
  * Function to get a list with the current points for all students of one class
  */
  function get_mumie_grades($courseid, $js = null){
  	global $CFG;
  	if(!$js){
  		$js = new JapsSynchronise();
  	}
  	//workaround - we do not want to be redirected because not logged in:
  	$login_response = $js->login();
  	
  	$cocoonPath = "protected/data/total-class-grades";
  	$class_sync_id = 'moodle-'.$CFG->prefix.'course-'.$courseid;
    // Starting the syncronisation and getting the result
    $response = $js->get($cocoonPath.'?sync-id='.$class_sync_id);
    $filteredResponse = filter_response($response);
    $grade_array = filter_response_to_array($filteredResponse, 'points');
    return $grade_array;
  }

/**************************************************************************************
 * 								Help Functions
 **************************************************************************************/

/**
  * Function to filter an answer of JapsSynchronise to string
  * 
  * @param $array - object of the unfiltered array
  */
 	function filter_response($array) {
   $result = "";
   if(is_array($array) || is_object($array)){
    foreach($array as $key => $value) {
        if($key == "body"){
                return $value;
            }
        }
        if(is_array($value) || is_object($value)) {
            dump_array($value);
        }
    }
    else {
        return "error";
        //return var_dump($array);
    }
}

/**
 * Function to get an array as a list out of a MUMIE response
 */
 function filter_response_to_array($response, $mode) {
   global $CFG;
   $xml = simplexml_load_string($response);
   $array = array();
   switch($mode){
   case 'courses':
	   foreach ($xml->children('http://www.mumie.net/xml-namespace/document/metainfo') as $item) { 
		  $course_attributes = $item->attributes();
		  $course_id = (int)$course_attributes['id'];
		  $newcourse = new object();
		  $newcourse->id = $course_id;
		  $newcourse->name = (string)$item->name;
		  if($item->class){
		  	$newcourse->alreadyused = true;
		  } else{
		  	$newcourse->alreadyused = false;
		  }
		  array_push($array, $newcourse);
		}
		break;
   case 'points':
   		$iterator = 0;
   		$user_sync_id_prefix = 'moodle-'.$CFG->prefix.'user-';
   		$user_sync_id_prefix_length = strlen($user_sync_id_prefix);
   		foreach ($xml->children('http://www.mumie.net/xml-namespace/grades') as $item) { 
		  	$itemlength = count($xml->children('http://www.mumie.net/xml-namespace/grades'));
		  	++$iterator;
		  	if($iterator<$itemlength){
			  	$grade_attributes = $item->attributes();
			  	$user_sync_id = (string)$grade_attributes['sync_id'];
			  	$new_user_grade = new object();
			  	$new_user_grade->userid = substr($user_sync_id, $user_sync_id_prefix_length);
			  	$new_user_grade->current_points = (double)$grade_attributes['points'];
			  	$new_user_grade->edited_problems = (int)$grade_attributes['edited_problems'];
			  	$new_user_grade->corrected_problems = (int)$grade_attributes['corrected_problems'];
			  	array_push($array, $new_user_grade);
		  	} elseif($iterator==$itemlength){
		  		$grade_attributes = $item->attributes();
		  		$mumie_course = new object();
		  		$mumie_course->course_name = (string)$grade_attributes['course_name'];
		  		$mumie_course->total_problems = (int)$grade_attributes['total_problems'];
		  		$mumie_course->total_points = (int)$grade_attributes['total_points'];
		  		array_push($array, $mumie_course);
		  	}
		} 
		break;
   }
	return $array;
}

/**
 * Function to var_dump an array
 * 
 * @param $array - array to be dumped out
 */
function dump_array($array) {
   $result = "";
   if(is_array($array) || is_object($array)){
	foreach($array as $key => $value) {
		if(is_array($value) || is_object($value)) {
			//$result .= "Array:$key =>" . dump_array($array);
		}
		else {
			$result .= "\$key:$key = \$value:$value\n";
		}
	}
    return $result;
    }
    else {
    	return "";
    }

}

/**
 * Function to write some information into a logfile
 */
function logoutput($filteredResponse, $order, $syncData){
	global $CFG;
	if($LOGG = fopen($CFG->dirroot.'/mod/mumiemodule/logs/hook_log.txt', 'a')){
		fputs($LOGG, date("F d Y h:i:s A").": ");
		fputs($LOGG, rtrim($filteredResponse)." bei ".$order);
		if(rtrim($filteredResponse) != "OK"){
			fputs($LOGG, "\n"."SyncData war: ");
			fputs($LOGG, dump_array($syncData));
		}
		fputs($LOGG, "\n");
	}
}

/**
 * yet another function to write information and the current time into logfile
 */
function logout_time($nachricht=''){
	global $CFG;
	if($LOGG = fopen($CFG->dirroot.'/mod/mumiemodule/logs/hook_log.txt', 'a')){
		fputs($LOGG, $nachricht);
		fputs($LOGG, "aktuelle Zeit: ".date("F d Y h:i:s A"));
		fputs($LOGG, "\n");
	}
}
 
 
?>
