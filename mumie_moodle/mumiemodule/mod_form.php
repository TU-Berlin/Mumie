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

global $CFG;
require_once $CFG->dirroot.'/course/moodleform_mod.php';
require_once 'internalchecks.php';

class mod_mumiemodule_mod_form extends moodleform_mod {

	function definition() {
		global $USER;
		global $CFG;
		global $COURSE;

		//business-rules for the activity
		if(!isset($CFG->syncServer) || !isset($CFG->syncUser) || !isset($CFG->syncPassword)){ //configuration settings
			error(get_string('nosettings', 'mumiemodule'));
		}
		//right now, we do not allow more than one activity per class - so:
		if(record_exists('mumiemodule', 'course', $COURSE->id)){
			error(get_string('justonemod', 'mumiemodule'), "view.php?id=".$COURSE->id);
			break;
		}
		/*if (!get_groups($COURSE->id)) { //at least one group
			error(get_string("nogroup", "mumiemodule"), "../course/view.php?id=".$COURSE->id);
			}*/
		//groups are not allowed to have more than one tutor (actually a group should have exactly one)
		$coursegroups = groups_get_all_groups($COURSE->id);
		$fields = 'u.id';
		$context = get_context_instance(CONTEXT_COURSE, $COURSE->id);
		foreach($coursegroups as $coursegroup){
			$grouptutors = get_users_by_capability($context, 'mod/mumiemodule:tutorize', $fields, '', '', '', $coursegroup->id);
			if(count($grouptutors)>1){
				error(get_string('toomuchtutors', 'mumiemodule'), "../group/index.php?id=".$COURSE->id);
				break;
			}
		}
		//a student must not be in more than one group when creating an instance of mumiemodule:
		$coursestudents = get_users_by_capability($context, 'mod/mumiemodule:participate', 'u.id');
		foreach($coursestudents as $coursestudent){
			$studentsgroups = groups_get_all_groups($COURSE->id, $coursestudent->id);
			if(count($studentsgroups)>1){
				error(get_string('justonegroup', 'mumiemodule'), "../group/index.php?id=".$COURSE->id);
				break;
			}
		}

		$mform =& $this->_form;

		$mform->addElement('header', 'general', get_string('general', 'form'));

		//name, inputfield
		$mform->addElement('text', 'name', 'Name', array('size'=>'64'));
		$mform->setType('name', PARAM_TEXT);
		$mform->addRule('name', null, 'required', null, 'client');

		//description, textarea
		$mform->addElement('htmleditor', 'description', get_string('description', 'mumiemodule'));
		$mform->setType('description', PARAM_RAW);
		$mform->setHelpButton('description', array('writing', 'questions', 'richtext'), false, 'editorhelpbutton');

		//mumie-courses, combobox
		$mumie_courses = get_mumie_courses();
		$selArray = array();
		//just for later checks:
		$alreadyusedcourses = '';
		
		foreach ($mumie_courses as $mumie_course) {
			$selArray[$mumie_course->id]=$mumie_course->name;
			
			//for having a string-list of courses, that are already used elsewhere:
			if($mumie_course->alreadyused===true){
				$alreadyusedcourses.=$mumie_course->id.' ';
			}
		}
		$mform->addElement('select', 'mumie_course_id', get_string('mumiecourse', 'mumiemodule'), $selArray);
		//small workaround to give the IDs of the courses already used to our add_instance function:
		$mform->addElement('hidden', 'mumie_courses_alreadyused', $alreadyusedcourses);
		
		//common module settings
		$this->standard_coursemodule_elements();

		//action buttons
		$this->add_action_buttons();
	}
}
?>