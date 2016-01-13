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
<?php // $Id: index.php,v 1.1 2007/12/21 12:05:42 ritter Exp $
/**
 * This page lists all the instances of mumiemodule in a particular course
 *
 * @author PR
 * @version $Id: index.php,v 1.1 2007/12/21 12:05:42 ritter Exp $
 * @package newmodule
 **/


    require_once("../../config.php");
    require_once("lib.php");

    $id = required_param('id', PARAM_INT);   // course

    if (! $course = get_record("course", "id", $id)) {
        error("Course ID is incorrect");
    }

    require_login($course->id);

    add_to_log($course->id, "mumiemodule", "view all", "index.php?id=$course->id", "");


/// Get all required stringsmumiemodule

    $strmumiemodules = get_string("modulenameplural", "mumiemodule");
    $strmumiemodule  = get_string("modulename", "mumiemodule");
    
    //neu von PR:
    $strgrade = get_string("grade");


/// Print the header

    if ($course->category) {
        $navigation = "<a href=\"../../course/view.php?id=$course->id\">$course->shortname</a> ->";
    } else {
        $navigation = '';
    }

    print_header("$course->shortname: $strmumiemodules", "$course->fullname", "$navigation $strmumiemodules", "", "", true, "", navmenu($course));

/// Get all the appropriate data

    if (! $mumiemodules = get_all_instances_in_course("mumiemodule", $course)) {
        notice("There are no mumiemodules", "../../course/view.php?id=$course->id");
        die;
    }

/// Print the list of instances (your module will probably extend this)

    $timenow = time();
    $strname  = get_string("name");
    $strweek  = get_string("week");
    $strtopic  = get_string("topic");

    if ($course->format == "weeks") {
        $table->head  = array ($strweek, $strname, $strgrade);
        $table->align = array ("center", "left");
    } else if ($course->format == "topics") {
        $table->head  = array ($strtopic, $strname, $strgrade);
        $table->align = array ("center", "left", "left", "left");
    } else {
        $table->head  = array ($strname, $strgrade);
        $table->align = array ("left", "left", "left");
    }

    foreach ($mumiemodules as $mumiemodule) {
        if (!$mumiemodule->visible) {
            //Show dimmed if the mod is hidden
            $link = "<a class=\"dimmed\" href=\"view.php?id=$mumiemodule->coursemodule\">$mumiemodule->name</a>";
        } else {
            //Show normal if the mod is visible
            $link = "<a href=\"view.php?id=$mumiemodule->coursemodule\">$mumiemodule->name</a>";
        }

        if ($course->format == "weeks" or $course->format == "topics") {
            $table->data[] = array ($mumiemodule->section, $link);
        } else {
            $table->data[] = array ($link);
        }
    }

    echo "<br />";

    print_table($table);

/// Finish the page

    print_footer($course);

?>
