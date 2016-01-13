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
<?php // $Id: events.php,v 1.1 2007/12/21 12:07:34 ritter Exp $

$handlers = array (

    'user_updated' => array (
        'handlerfile'      => '/mod/mumiemodule/handler.php',
        'handlerfunction'  => 'user_updated_handler',
        'schedule'         => 'instant'
    ),
    'password_changed' => array (
        'handlerfile'      => '/mod/mumiemodule/handler.php',
        'handlerfunction'  => 'password_changed_handler',
        'schedule'         => 'instant'
    ),
    'user_deleted' => array (
        'handlerfile'      => '/mod/mumiemodule/handler.php',
        'handlerfunction'  => 'user_deleted_handler',
        'schedule'         => 'instant'
    ),
    'course_updated' => array (
        'handlerfile'      => '/mod/mumiemodule/handler.php',
        'handlerfunction'  => 'course_updated_handler',
        'schedule'         => 'instant'
    ),
    'course_deleted' => array (
        'handlerfile'      => '/mod/mumiemodule/handler.php',
        'handlerfunction'  => 'course_deleted_handler',
        'schedule'         => 'instant'
    ),
    'category_updated' => array (
        'handlerfile'      => '/mod/mumiemodule/handler.php',
        'handlerfunction'  => 'category_updated_handler',
        'schedule'         => 'instant'
    ),
    'category_deleted' => array (
        'handlerfile'      => '/mod/mumiemodule/handler.php',
        'handlerfunction'  => 'category_deleted_handler',
        'schedule'         => 'instant'
    ),
    'group_created' => array (
        'handlerfile'      => '/mod/mumiemodule/handler.php',
        'handlerfunction'  => 'group_created_handler',
        'schedule'         => 'instant'
    ),
    'group_updated' => array (
        'handlerfile'      => '/mod/mumiemodule/handler.php',
        'handlerfunction'  => 'group_updated_handler',
        'schedule'         => 'instant'
    ),
	'group_user_added' => array (
        'handlerfile'      => '/mod/mumiemodule/handler.php',
        'handlerfunction'  => 'group_user_added_handler',
        'schedule'         => 'instant'
    ),
	'group_user_removed' => array (
        'handlerfile'      => '/mod/mumiemodule/handler.php',
        'handlerfunction'  => 'group_user_removed_handler',
        'schedule'         => 'instant'
    ),
    'group_deleted' => array (
        'handlerfile'      => '/mod/mumiemodule/handler.php',
        'handlerfunction'  => 'group_deleted_handler',
        'schedule'         => 'instant'
    )
);

?>