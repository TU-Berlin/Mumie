
INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student1'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_selftest'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/selftest'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_determinante_mc'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/determinante'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_selftest'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/selftest')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_determinante_mc'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/determinante')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
</data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student1'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_selftest'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/selftest'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_determinante_mc'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/determinante'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_selftest'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/selftest')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_determinante_mc'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/determinante')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="choices-1"><data name="choice-3">true</data><data name="choice-2">true</data><data name="choice-1">false</data><data name="choice-10">true</data><data name="choice-7">true</data><data name="choice-6">false</data><data name="choice-5">true</data><data name="choice-4">true</data><data name="choice-9">true</data><data name="choice-8">false</data><data name="choice-11">true</data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student1'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_selftest'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/selftest'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_determinante_geometrie'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/determinante'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_selftest'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/selftest')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_determinante_geometrie'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/determinante')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
        </data>
      </unit>
    </unit>
  </unit>
</data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student1'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_selftest'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/selftest'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_determinante_geometrie'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/determinante'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_selftest'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/selftest')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_determinante_geometrie'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/determinante')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="currentSubtask">5</data></unit><unit name="subtask_1"><data name="flaeche"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="real" type="op-number"><mn field="real" type="number">2</mn></mrow></data><data name="laenge"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="real" type="op-number"><msqrt><mn field="real" type="number">2</mn></msqrt></mrow></data></unit><unit name="subtask_2"><data name="vector_b"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="real" type="vector">
  <mn field="real" type="number">1</mn>
  <mn field="real" type="number">2</mn>
</mrow></data></unit><unit name="subtask_3"><data name="vector_c"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="real" type="vector">
  <mn field="real" type="number">1</mn>
  <mn field="real" type="number">1</mn>
</mrow></data></unit><unit name="subtask_4"><data name="vector_d"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="real" type="vector">
  <mn field="real" type="number">-1</mn>
  <mn field="real" type="number">1</mn>
</mrow></data></unit><unit name="subtask_5"><data name="vector_e"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="real" type="vector">
  <mn field="real" type="number">1</mn>
  <mn field="real" type="number">-1</mn>
</mrow></data></unit></unit></unit></data_sheet>');
