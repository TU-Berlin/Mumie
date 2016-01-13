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
<?php  // $Id: view.php,v 1.2 2008/03/28 15:25:53 ritter Exp $
/**
 * This page prints the current sudents points of a current instance of a mumiemodule
 * and links to the MUMIE itself
 * 
 * @author PR
 * @version $Id: view.php,v 1.2 2008/03/28 15:25:53 ritter Exp $
 **/


    require_once("../../config.php");
    require_once("lib.php");

    $id = optional_param('id', 0, PARAM_INT); // Course Module ID, or
    $a  = optional_param('a', 0, PARAM_INT);  // mumiemodule ID
    $changegroup = optional_param('group', -1, PARAM_INT);   // choose the current group

    if ($id) {
        if (! $cm = get_record("course_modules", "id", $id)) {
            error("Course Module ID was incorrect");
        }
    
        if (! $course = get_record("course", "id", $cm->course)) {
            error("Course is misconfigured");
        }
    
        if (! $mumiemodule = get_record("mumiemodule", "id", $cm->instance)) {
            error("Course module is incorrect");
        }

    } else {
        if (! $mumiemodule = get_record("mumiemodule", "id", $a)) {
            error("Course module is incorrect");
        }
        if (! $course = get_record("course", "id", $mumiemodule->course)) {
            error("Course is misconfigured");
        }
        if (! $cm = get_coursemodule_from_instance("mumiemodule", $mumiemodule->id, $course->id)) {
            error("Course Module ID was incorrect");
        }
    }

    require_login($course->id);
    $context = get_context_instance(CONTEXT_MODULE, $cm->id);
    
    //get the current grades out of the MUMIE
    $mumie_grades = get_mumie_grades($course->id);
    $array_length = count($mumie_grades);
    $iterator = 0;
    foreach($mumie_grades as $mumie_grade){
    	$iterator++;
    	if($iterator<$array_length){
    		$mumiemodule_student = new object();
    		$mumiemodule_student->id = get_field('mumiemodule_students', 'id', 'mumiemodule', $mumiemodule->id, 'userid', $mumie_grade->userid);
    		$mumiemodule_student->mumiemodule = $mumiemodule->id;
    		$mumiemodule_student->userid = $mumie_grade->userid;
    		$mumiemodule_student->groupid = get_field('mumiemodule_students', 'groupid', 'id', $mumiemodule_student->id);
    		$mumiemodule_student->grade = $mumie_grade->current_points;
    		//$mumiemodule_student->lastexercise
    		$mumiemodule_student->timemodified = time();
    		update_record('mumiemodule_students', $mumiemodule_student);
    	}else{
    		$mumiemodule->grade = $mumie_grade->total_points;
    		update_record('mumiemodule', $mumiemodule);
    	}
    }

    add_to_log($course->id, "mumiemodule", "view", "view.php?id=$cm->id", "$mumiemodule->id");

/// Print the page header

    if ($course->category) {
        $navigation = "<a href=\"../../course/view.php?id=$course->id\">$course->shortname</a> ->";
        $navigation .= "<a href=\"view.php?id=$id\">$mumiemodule->name</a> ->";
    } else { //when does this happen???
        $navigation = '';
    }

    $strmumiemodules = get_string("modulenameplural", "mumiemodule");
    $strmumiemodule  = get_string("modulename", "mumiemodule");

    print_header("$course->shortname: $mumiemodule->name->current student points", "$course->fullname",
                 "$navigation <a href=index.php?id=$course->id>$strmumiemodules</a>", 
                  "", "", true, '&nbsp;', 
                  navmenu($course, $cm));
                 
   

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/// Print the main part of the page
print_heading(get_string("modulename", "mumiemodule"));

//Link to enter the MUMIE:
$mumielink = "";
	print_simple_box_start('right');
	echo '<script type="text/javascript">function openmumie2() {document.mumieform2.target = "_blank";document.mumieform2.submit();}</script>'
		.'<form name="mumieform2" action="'.$CFG->syncServer.'/public/auth/login" method="post">'
		.'<input type="hidden" name="name" value="'.$USER->username.'" />' .
				'<input type="hidden" name="password" value="'.$USER->password.'" />' .
				'<input type="hidden" name="resource" value="'.$CFG->syncServer.'/protected/auth/login-successful" /><a href="javascript:openmumie2()" > ' .
				get_string('entermumie', 'mumiemodule').'</a>'
		.'</form>';
	print_simple_box_end();
//end of entering
//show info
    if (!empty($mumiemodule->description)) {
                print_box(format_text($mumiemodule->description), 'generalbox', 'description');
            }

if(has_capability('mod/mumiemodule:manage', $context)){
    $datetime =    date("F d,Y g:i a");
    
    /// Some capability checks.
    //shall we see hidden activities?
    if (empty($cm->visible) and !has_capability('moodle/course:viewhiddenactivities', $context)) {
        notice(get_string("activityiscurrentlyhidden"));
    }
    
    //check for groupmode
    $groupmode = groupmode($course, $cm);
    $currentgroup = get_and_set_current_group($course, $groupmode, $changegroup);
    
    if ($groupmode == SEPARATEGROUPS && ($currentgroup === false) &&
            !has_capability('moodle/site:accessallgroups', $context)) {
        notice(get_string('notingroup', 'mumiemodule'));
    }
    /// Okay, we can show the grades. Log the mumiemodule view.
    if ($cm->id) {
        add_to_log($course->id, "mumiemodule", "view grades", "view.php?id=$cm->id", "$mumiemodule->id", $cm->id);
    } else {
        add_to_log($course->id, "mumiemodule", "view grades", "view.php?f=$forum->id", "$mumiemodule->id");
    }
    /// Now we need a menu for separategroups as well!
    if ($groupmode == VISIBLEGROUPS || ($groupmode
            && has_capability('moodle/site:accessallgroups', $context))) {
        
       if($groups = get_groups($course->id)){  
            print_box_start('groupmenu');
            print_group_menu($groups, $groupmode, $currentgroup, "$CFG->wwwroot/mod/mumiemodule/view.php?id=$cm->id");
            print_box_end(); // groupmenu
        }
    }/// Only print menus the student is in any course
    else if ($groupmode == SEPARATEGROUPS){
        $validgroups = array();
        // Get all the groups this guy is in in this course

        if ($p = user_group($course->id,$USER->id)){
            /// Extract the name and id for the group
            foreach ($p as $index => $object){
                $validgroups[$object->id] = $object->name;
            }
            /// Print them in the menu
            print_box_start('groupmenu');
            print_group_menu($validgroups, $groupmode, $currentgroup, "view.php?id=$cm->id",0);
            print_box_end(); // groupmenu
        }
    }
            echo '<br />';
            if (!empty($showall)) {
                show_grades($currentgroup, $cm, $course, $mumiemodule);
            } else {
                show_grades($currentgroup, $cm, $course, $mumiemodule);
            }    
}else if(has_capability('mod/mumiemodule:participate', $context)){

	if (!record_exists('mumiemodule_students', 'mumiemodule', $mumiemodule->id, 'userid', $USER->id)){ 
		error ("You have to take part in this activity to take a look! So you should be part of a group.", "../../course/view.php?id=".$COURSE->id);
	}
	$groupmode = groupmode($course, $cm);
	$currentgroup = get_and_set_current_group($course, $groupmode, $changegroup);

	if ($groupmode == SEPARATEGROUPS && ($currentgroup === false) &&
	!has_capability('moodle/site:accessallgroups', $context)) {
		notify(get_string('notingroupstudent', 'mumiemodule'));
	}

	$currentgrade = get_field('mumiemodule_students', 'grade', 'mumiemodule', $mumiemodule->id, 'userid', $USER->id);
	$currentgrade .= ' / '.$mumiemodule->grade;
	print_box("Current points: ".$currentgrade);
}else {
   error("You do not have the right permission!");
}

/// Finish the page
    print_footer($course);
    
    /**
     * Function for showing the students points to a creator, teacher or trainer
     */
     function show_grades($currentgroup, $cm, $course, $mumiemodule){
     	global $CFG, $USER, $db;
     	
     	//needed? I am not sure
     	$page    = optional_param('page', 0, PARAM_INT);
     	
     	$context = get_context_instance(CONTEXT_COURSE, $course->id);
     	
     	/// Get all teachers and students
        if ($currentgroup) {
            $users = get_records('mumiemodule_students', 'groupid', $currentgroup, '', 'userid');
            //$users = get_group_users($currentgroup);
        } else {
            //$context = get_context_instance(CONTEXT_MODULE, $cm->id);
            $course_groups = groups_get_all_groups($course->id, 0, 0, 'g.id');
            $groups = array();
            foreach ($course_groups as $group){
            	$groups[] = $group->id;
            }
            $users = get_users_by_capability($context, 'mod/mumiemodule:participate', '', '', '', '', $groups); // everyone with this capability set to non-prohibit
            //TODO we need a user-list with just students that are in groups
        }

        $tablecolumns = array('picture', 'fullname', 'grade', 'timemodified');
        $tableheaders = array('', get_string('fullname'), get_string('grade', 'mumiemodule'), get_string('lastmodified', 'mumiemodule').' ('.$course->student.')');

        require_once($CFG->libdir.'/tablelib.php');
        $table = new flexible_table('mumiemodule-current-grades');
                        
        $table->define_columns($tablecolumns);
        $table->define_headers($tableheaders);
        $table->define_baseurl($CFG->wwwroot.'/mod/mumiemodule/view.php?id='.$cm->id.'&amp;currentgroup='.$currentgroup);
                
        $table->sortable(true, 'lastname');//sorted by lastname by default
        $table->collapsible(true);
        $table->initialbars(true);
        
        $table->column_suppress('picture');
        $table->column_suppress('fullname');
        
        $table->column_class('picture', 'picture');
        $table->column_class('fullname', 'fullname');
        $table->column_class('grade', 'grade');
        $table->column_class('timemodified', 'timemodified');
        
        $table->set_attribute('cellspacing', '0');
        $table->set_attribute('id', 'attempts');
        $table->set_attribute('class', 'grades');
        $table->set_attribute('width', '90%');
        $table->set_attribute('align', 'center');
            
        // Start working -- this is necessary as soon as the niceties are over
        $table->setup();

    /// Check to see if groups are being used in this assignment

        /*if (!$teacherattempts) { //@toDo: check variable!!!
            $teachers = get_course_teachers($course->id);
            if (!empty($teachers)) {
                $keys = array_keys($teachers);
            }
            foreach ($keys as $key) {
                unset($users[$key]);
            }
        }*/
        
         /// Construct the SQL

        if ($where = $table->get_sql_where()) {
            $where .= ' AND ';
        }

        if ($sort = $table->get_sql_sort()) {
            $sort = ' ORDER BY '.$sort;
        }

        $select = 'SELECT u.id, u.firstname, u.lastname, u.picture, 
                          s.id AS mumiepartsid, s.grade, 
                          s.timemodified ';
        $sql = 'FROM '.$CFG->prefix.'user u '.
               'LEFT JOIN '.$CFG->prefix.'mumiemodule_students s ON u.id = s.userid 
                                                                  AND s.mumiemodule = '.$mumiemodule->id.' '.
               'WHERE '.$where.'u.id IN ('.implode(',', array_keys($users)).') ';
        
        $table->pagesize(10, count($users));
        
        ///offset used to calculate index of student in that particular query, needed for the pop up to know who's next
        $offset = $page * 10;
        $strupdate = get_string('update');
        $strgrade  = get_string('grade');
        $grademenu = make_grades_menu($mumiemodule->grade);
        
        if (($ausers = get_records_sql($select.$sql.$sort, $table->get_page_start(), $table->get_page_size())) !== false) {
        //if (($ausers = get_records($select.$sql.$sort, $table->get_page_start(), $table->get_page_size())) !== false) { 
            
            foreach ($ausers as $auser) {
            /// Calculate user status
                $auser->status = 1;
                $picture = print_user_picture($auser->id, $course->id, $auser->picture, false, true);
                
                    $studentmodified = '<div id="ts'.$auser->id.'">'./*$this->print_student_answer($auser->id).*/userdate($auser->timemodified).'</div>';
                    $grade = '<div id="g'.$auser->id.'">'.$auser->grade.'/'.$mumiemodule->grade.'</div>';
				
                $buttontext = ($auser->status == 1) ? $strupdate : $strgrade;
                                   
                ///No more buttons, we use popups ;-).
                $button = link_to_popup_window ('/mod/mumiemodule/view.php?id='.$cm->id.'&amp;userid='.$auser->id.'&amp;mode=single'.'&amp;offset='.$offset++, 
                                                'grade'.$auser->id, $buttontext, 500, 780, $buttontext, 'none', true, 'button'.$auser->id);

                $status  = '<div id="up'.$auser->id.'" class="s'.$auser->status.'">'.$button.'</div>';
                
                $row = array($picture, fullname($auser), $grade, $studentmodified);
                $table->add_data($row);
            }
        }
        
     /**
     *  Return a grade in user-friendly form, whether it's a scale or not
     *  
     * @param $grade
     * @return string User-friendly representation of grade
     */
    function display_grade($grade, $mumiemodule) {

        $scalegrades = array();   

        if ($mumiemodule->grade >= 0) {    // Normal number
            if ($grade == -1) {
                return '-';
            } else {
                return $grade.' / '.$mumiemodule->grade;
            }

        } else {                                // Scale
            if (empty($scalegrades[$mumiemodule->id])) {
                if ($scale = get_record('scale', 'id', -($mumiemodule->grade))) {
                    $scalegrades[$mumiemodule->id] = make_menu_from_list($scale->scale);
                } else {
                    return '-';
                }
            }
            if (isset($scalegrades[$mumiemodule->id][$grade])) {
                return $scalegrades[$mumiemodule->id][$grade];
            }
            return '-';
        }
    }
        
        
        
        $table->print_html();  /// Print the whole table
       
     }
?>
