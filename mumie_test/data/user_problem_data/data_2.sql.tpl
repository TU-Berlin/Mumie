
INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = '@login-name@'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_lineare_unabhaengigkeit_in_rn'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume_rn_cn_kn'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_lineare_unabhaengigkeit_in_rn'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume_rn_cn_kn')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_4"><data name="selected_1">common/problem/vectors/vector_2</data><data name="selected_2">common/problem/vectors/vector_3</data><data name="selected_3">common/problem/vectors/vector_6</data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = '@login-name@'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_linearkombination_in_r2'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume_rn_cn_kn'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_linearkombination_in_r2'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume_rn_cn_kn')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="coeff1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">-7</mn></mrow><mrow><mn field="real" type="number">3</mn></mrow></mfrac></mrow></mrow></data><data name="coeff2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="real" type="op-number"><mn field="real" type="number">-1</mn></mrow></data><data name="coeff3"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="real" type="op-number"><mn field="real" type="number">1</mn></mrow></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = '@login-name@'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_vr-operationen_in_cn'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume_rn_cn_kn'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_vr-operationen_in_cn'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume_rn_cn_kn')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_5"><data name="vsum5"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="complex-rational" type="vector">
  <mn field="complex-rational" type="number">1</mn>
  <mn field="complex-rational" type="number">2</mn>
  <mn field="complex-rational" type="number">3</mn>
</mrow></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = '@login-name@'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_lineare-unabhaenigkeit_schrift'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume_rn_cn_kn'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_lineare-unabhaenigkeit_schrift'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume_rn_cn_kn')),

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
   (SELECT id FROM users WHERE login_name = '@login-name@'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_lineare_unabhaengigkeit_in_rn'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume_rn_cn_kn'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_lineare_unabhaengigkeit_in_rn'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume_rn_cn_kn')),

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
   (SELECT id FROM users WHERE login_name = '@login-name@'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_linearkombination_in_r2'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume_rn_cn_kn'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_linearkombination_in_r2'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume_rn_cn_kn')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector_1">
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">-3</mn>
        </data>
      </unit>
      <unit name="vector_2">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">4</mn>
        </data>
      </unit>
      <unit name="vector_3">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">2</mn>
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
   (SELECT id FROM users WHERE login_name = '@login-name@'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_vr-operationen_in_cn'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume_rn_cn_kn'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_vr-operationen_in_cn'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume_rn_cn_kn')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="subtask_1">
        <unit name="vector_1">
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">0</mn>
          </data>
        </unit>
        <unit name="vector_2">
          <unit name="re-components">
            <data name="re-component_1">
              <mn xmlns="http://www.w3.org/1998/Math/MathML">-3</mn>
            </data>
          </unit>
        </unit>
      </unit>
    </unit>
  </unit>
</data_sheet>
');
