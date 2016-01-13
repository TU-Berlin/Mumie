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
 * Library of classes and functions
 * for module mumiemodule
 *
 * @author PR
 * @package mumiemodule
 **/
 
 
class Semester {
	 var $syncid = "";
	 var $name = "";
	 var $description = "";

	function __construct($syncid, $name, $description) {
		$this->syncid = $syncid;
		$this->name = $name;
		$this->description = $description;
	}

	 function setDescription($description) {
		$this->description = $description;
	}
	
	function setSyncId($syncid) {
		$this->syncid = $syncid;
	}
}

class MumieClass {
	 var $syncid = "";
	 var $name = "";
	 var $description = "";
	 var $semester = null;
	 var $lecturers = null;
	 var $tutorials = null;
	 var $mumie_course_id = "";

	function __construct($syncid, $name, $description, $semester, $lecturers, $tutorials, $mumie_course_id) {
		$this->syncid = $syncid;
		$this->name = $name;
		$this->description = $description;
		$this->semester = $semester;
		$this->lecturers = $lecturers;
		$this->tutorials = $tutorials;
		$this->mumie_course_id = $mumie_course_id;
	}
	
	function setDescription($description) {
		$this->description = $description;
	}
	
	function setSyncId($syncid) {
		$this->syncid = $syncid;
	}

	function addLecturer($lecturer) {
		array_push($this->lecturers, $lecturer);
	}

	function setSemester($semester){
		$this->semester = $semester;
	}
	
	function addTutorial($tutorial) {
		array_push($this->tutorials, $tutorial);
	}
	
	function setMumieCourseId($mumie_course_id){
		$this->mumie_course_id = $mumie_course_id;
	}
}

class Tutorial {
	var $syncid = "";
	var $name = "";
	var $description = "";
	var $tutor = null;
	var $students = null;

	function __construct($syncid, $name, $description, $tutor, $students) {
		$this->syncid = $syncid;
		$this->name = $name;
		$this->description = $description;
		$this->tutor = $tutor;
		$this->students = $students;
	}
	
	function setDescription($description) {
		$this->description = $description;
	}
	
	function setSyncId($syncid) {
		$this->syncid = $syncid;
	}

	 function setTutor($tutor) {
		$this->tutor = $tutor;
	}

	 function addStudent($student) {
		array_push($this->students, $student);
	}
}

class User {
	var $syncid = "";
	var $loginname = ""; 
	var $passwordencrypted = "";
	var $firstname = "";
	var $surname = "";
	var $matrnumber = "X";

	function __construct($syncid, $loginname, $passwordencrypted, $firstname, $surname, $matrnumber) {
		$this->syncid = $syncid;
		$this->loginname = $loginname;
		$this->passwordencrypted = $passwordencrypted;
		$this->firstname = $firstname;
		$this->surname = $surname;
		$this->matrnumber = $matrnumber;
	}
	
	function setpasswordencrypted($passwordencrypted){
		$this->passwordencrypted = $passwordencrypted;
	}
	
	function setFirstname($firstname){
		$this->firstname = $firstname;
	}
	
	function setSurname($surname){
		$this->surname = $surname;
	}
	
	function setMatrnumber($matrnumber){
		$this->matrnumber = $matrnumber;
	}

	 function getName() {
		return $this->name;
	}
}

class Lecturer extends User {

	function __construct($syncid, $loginname, $passwordencrypted, $firstname, $surname, $matrnumber) {
		parent::__construct($syncid, $loginname, $passwordencrypted, $firstname, $surname, $matrnumber);
	}

}

class Tutor extends User {
	function __construct($syncid, $loginname, $passwordencrypted, $firstname, $surname, $matrnumber) {
		parent::__construct($syncid, $loginname, $passwordencrypted, $firstname, $surname, $matrnumber);
	}
}

class Student extends User {
	function __construct($syncid, $loginname, $passwordencrypted, $firstname, $surname, $matrnumber) {
		parent::__construct($syncid, $loginname, $passwordencrypted, $firstname, $surname, $matrnumber);
	}
}
?>