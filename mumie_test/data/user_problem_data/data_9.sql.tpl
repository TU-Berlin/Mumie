
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
       WHERE pure_name='wks_summary_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_lineare_unabhaengigkeit_in_rn'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume_rn_cn_kn'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_summary_hwk'
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
       WHERE pure_name='wks_diagonalisierbare_matrizen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_diagonalisierbare_matrizen'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diagonalisierbare_matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_diagonalisierbare_matrizen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_diagonalisierbare_matrizen'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diagonalisierbare_matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="l1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="real" type="op-number"><mn field="real" type="number">1</mn></mrow></data><data name="l2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="real" type="op-number"><mn field="real" type="number">-2</mn></mrow></data></unit><unit name="subtask_2"><data name="matrix_invers"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="op-number" type="matrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">3</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-4</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-2</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">3</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_3"><data name="matrix_product"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="op-number" type="matrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-2</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_4"><data name="selected_0">richtig</data><data name="selected_1">falsch</data><data name="selected_2">richtig</data><data name="selected_3">richtig</data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_diagonalisierbare_matrizen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_housholder'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diagonalisierbare_matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_diagonalisierbare_matrizen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_housholder'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diagonalisierbare_matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="matrixx"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="op-number" type="matrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">3</mn></mrow><mrow><mn field="real" type="number">5</mn></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">4</mn></mrow><mrow><mn field="real" type="number">5</mn></mrow></mfrac></mrow></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">4</mn></mrow><mrow><mn field="real" type="number">5</mn></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">-3</mn></mrow><mrow><mn field="real" type="number">5</mn></mrow></mfrac></mrow></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_diagonalisierbare_matrizen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_diagonalisierbare_matrizen'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diagonalisierbare_matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_diagonalisierbare_matrizen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_diagonalisierbare_matrizen'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diagonalisierbare_matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <data name="lambda1">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
      </data>
      <data name="lambda2">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
      </data>
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
       WHERE pure_name='wks_diagonalisierbare_matrizen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_housholder'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diagonalisierbare_matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_diagonalisierbare_matrizen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_housholder'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diagonalisierbare_matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector_1">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-4</mn>
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
       WHERE pure_name='wks_vektorraeume_rn_cn_kn_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_linearkombination'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume_rn_cn_kn'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_rn_cn_kn_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_linearkombination'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume_rn_cn_kn')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="pre_value"><mn field="int" type="number" xmlns="http://www.w3.org/1998/Math/MathML">1</mn></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_vektorraeume_rn_cn_kn_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_vektorraum'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume_rn_cn_kn'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_rn_cn_kn_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_vektorraum'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume_rn_cn_kn')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="vec1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="rational" type="vector">
  <mn field="rational" type="number">-2</mn>
  <mfrac field="rational" type="number"><mn field="int" type="number">15</mn><mn field="int" type="number">4</mn></mfrac>
</mrow></data></unit><unit name="subtask_2"><data name="vec2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="rational" type="vector">
  <mn field="rational" type="number">25</mn>
  <mn field="rational" type="number">1</mn>
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
       WHERE pure_name='wks_vektorraeume_rn_cn_kn_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_linearkombination'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume_rn_cn_kn'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_rn_cn_kn_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_linearkombination'
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
       WHERE pure_name='wks_vektorraeume_rn_cn_kn_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_vektorraum'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume_rn_cn_kn'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_rn_cn_kn_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_vektorraum'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume_rn_cn_kn')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="subtask_1">
        <unit name="vector_1">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">0</mn>
          </data>
        </unit>
        <unit name="vector_2">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-2</mn>
          </data>
        </unit>
      </unit>
      <unit name="subtask_2">
        <unit name="vector_1">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">5</mn>
          </data>
        </unit>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">-2</mn>
        </data>
        <unit name="vector_2">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-10</mn>
          </data>
        </unit>
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
       WHERE pure_name='wks_matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_kern_bild_matrix-abbildung'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_kern_bild_matrix-abbildung'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="matrix_a"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="rational" type="matrix">
  <mtr>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">-1</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">3</mn>
    </mtd>
  </mtr>
</mtable></data><data name="a_v1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="rational" type="vector">
  <mn field="rational" type="number">3</mn>
  <mn field="rational" type="number">1</mn>
</mrow></data><data name="a_v2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="rational" type="vector">
  <mn field="rational" type="number">4</mn>
  <mn field="rational" type="number">2</mn>
</mrow></data><data name="a_w1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="rational" type="vector">
  <mn field="rational" type="number">0</mn>
  <mn field="rational" type="number">0</mn>
</mrow></data><data name="a_w2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="rational" type="vector">
  <mn field="rational" type="number">0</mn>
  <mn field="rational" type="number">2</mn>
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
       WHERE pure_name='wks_matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_multiplikation_von_matrizen'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_multiplikation_von_matrizen'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><unit name="format_A1"><data name="rows"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="op-number"><mn field="int" type="number">2</mn></mrow></data><data name="columns"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="op-number"><mn field="int" type="number">3</mn></mrow></data></unit><unit name="format_A2"><data name="rows"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="op-number"><mn field="int" type="number">2</mn></mrow></data><data name="columns"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="op-number"><mn field="int" type="number">2</mn></mrow></data></unit><unit name="format_A3"><data name="rows"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="op-number"><mn field="int" type="number">2</mn></mrow></data><data name="columns"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="op-number"><mn field="int" type="number">2</mn></mrow></data></unit><unit name="format_A4"><data name="rows"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="op-number"><mn field="int" type="number">3</mn></mrow></data><data name="columns"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="op-number"><mn field="int" type="number">2</mn></mrow></data></unit><unit name="format_A5"><data name="rows"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="op-number"><mn field="int" type="number">2</mn></mrow></data><data name="columns"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="op-number"><mn field="int" type="number">3</mn></mrow></data></unit><unit name="format_A6"><data name="rows"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="op-number"><mn field="int" type="number">1</mn></mrow></data><data name="columns"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="op-number"><mn field="int" type="number">3</mn></mrow></data></unit><unit name="format_A7"><data name="rows"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="op-number"><mn field="int" type="number">2</mn></mrow></data><data name="columns"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="op-number"><mn field="int" type="number">1</mn></mrow></data></unit></unit><unit name="subtask_2"><data name="matrix_1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="real" type="matrix">
  <mtr>
    <mtd>
      <mn field="real" type="number">-6</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">1</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="real" type="number">1</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">-10</mn>
    </mtd>
  </mtr>
</mtable></data><data name="selectedMatrix_1">common/problem/matrices/matrix_2</data><data name="matrix_2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="real" type="matrix">
  <mtr>
    <mtd>
      <mn field="real" type="number">1</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">10</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="real" type="number">5</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">10</mn>
    </mtd>
  </mtr>
</mtable></data><data name="selectedMatrix_2">common/problem/matrices/matrix_3</data><data name="matrix_sum"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-50</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-49</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-90</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_3"><data name="matrix_1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="real" type="matrix">
  <mtr>
    <mtd>
      <mn field="real" type="number">7</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">8</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="real" type="number">-10</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">-1</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="real" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">-7</mn>
    </mtd>
  </mtr>
</mtable></data><data name="selectedMatrix_1">common/problem/matrices/matrix_4</data><data name="matrix_2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="real" type="matrix">
  <mtr>
    <mtd>
      <mn field="real" type="number">-5</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">7</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">9</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="real" type="number">-7</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">-7</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">-7</mn>
    </mtd>
  </mtr>
</mtable></data><data name="selectedMatrix_2">common/problem/matrices/matrix_5</data><data name="matrix_sum"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-91</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-7</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">7</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">57</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-63</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-83</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">49</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">49</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">49</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_4"><data name="matrix_1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="real" type="matrix">
  <mtr>
    <mtd>
      <mn field="real" type="number">4</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="real" type="number">2</mn>
    </mtd>
  </mtr>
</mtable></data><data name="selectedMatrix_1">common/problem/matrices/matrix_7</data><data name="matrix_2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="real" type="matrix">
  <mtr>
    <mtd>
      <mn field="real" type="number">-4</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">7</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">-10</mn>
    </mtd>
  </mtr>
</mtable></data><data name="selectedMatrix_2">common/problem/matrices/matrix_6</data><data name="matrix_sum"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-16</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">28</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-40</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-8</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">14</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-20</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_operationen_auf_und_mit_matrizen'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_operationen_auf_und_mit_matrizen'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">10</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">10</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_3"><data name="vector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector">
  <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
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
       WHERE pure_name='wks_matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_kern_bild_matrix-abbildung'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_kern_bild_matrix-abbildung'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector_1">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
        </data>
      </unit>
      <unit name="vector_2">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
        </data>
      </unit>
      <unit name="vector_3">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">4</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
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
       WHERE pure_name='wks_matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_klausuraufgabe_matrizen3'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_klausuraufgabe_matrizen3'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen')),

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
       WHERE pure_name='wks_matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_multiplikation_von_matrizen'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_multiplikation_von_matrizen'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">6</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-6</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-6</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">10</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-6</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-10</mn>
          </data>
        </unit>
        <unit name="matrix_3">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">10</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">5</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">10</mn>
          </data>
        </unit>
        <unit name="matrix_4">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">7</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">8</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-10</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-7</mn>
          </data>
        </unit>
        <unit name="matrix_5">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">7</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">9</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-7</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-7</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-7</mn>
          </data>
        </unit>
        <unit name="matrix_6">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-4</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">7</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-10</mn>
          </data>
        </unit>
        <unit name="matrix_7">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">4</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
        </unit>
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
       WHERE pure_name='wks_matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_operationen_auf_und_mit_matrizen'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_operationen_auf_und_mit_matrizen'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <data name="coefficient_1">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
      </data>
      <data name="coefficient_2">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
      </data>
      <data name="coefficient_3">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
      </data>
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
       WHERE pure_name='wks_determinante_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_determinante'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/determinante'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_determinante_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_determinante'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/determinante')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="det"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">150</mn></mrow></data></unit><unit name="subtask_2"><data name="det"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">-33</mn></mrow></data></unit><unit name="subtask_3"><data name="det"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">0</mn></mrow></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_determinante_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_determinante_geometrie'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/determinante'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_determinante_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_determinante_geometrie'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/determinante')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="flaeche"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">5</mn></mrow></data><data name="laenge"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><msqrt><mn field="real" type="number">5</mn></msqrt></mrow></data></unit><unit name="subtask_2"><data name="vector_b"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="real" type="vector">
  <mn field="real" type="number">0,5</mn>
  <mn field="real" type="number">1</mn>
</mrow></data></unit><unit name="subtask_3"><data name="vector_c"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="real" type="vector">
  <mn field="real" type="number">4</mn>
  <mn field="real" type="number">-2</mn>
</mrow></data></unit><unit name="subtask_4"><data name="vector_d"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="real" type="vector">
  <mn field="real" type="number">1</mn>
  <mn field="real" type="number">1</mn>
</mrow></data></unit><unit name="subtask_5"><data name="vector_e"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="real" type="vector">
  <mn field="real" type="number">0</mn>
  <mn field="real" type="number">-1</mn>
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
       WHERE pure_name='wks_determinante_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_kern_matrix-abbildung'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_determinante_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_kern_matrix-abbildung'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="op-number" type="matrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">236</mn></mrow><mrow><mn field="real" type="number">57</mn></mrow></mfrac></mrow></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">43</mn></mrow><mrow><mn field="real" type="number">39</mn></mrow></mfrac></mrow></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">70</mn></mrow><mrow><mn field="real" type="number">57</mn></mrow></mfrac></mrow></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_3"><data name="n"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="real" type="op-number"><mn field="real" type="number">5</mn></mrow></data><data name="set"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="tuple-set" field="rational" label="Kern(A) =" type="tuple-set"><mo>{</mo><mrow field="rational" type="op-number"><mi>s</mi></mrow><mrow class="column-vector" field="op-number" type="vector">
  <mrow field="rational" type="op-number"><mn field="rational" type="number">-1</mn></mrow>
  <mrow field="rational" type="op-number"><mn field="rational" type="number">0</mn></mrow>
  <mrow field="rational" type="op-number"><mn field="rational" type="number">1</mn></mrow>
  <mrow field="rational" type="op-number"><mn field="rational" type="number">0</mn></mrow>
  <mrow field="rational" type="op-number"><mn field="rational" type="number">0</mn></mrow>
</mrow><mo>+</mo><mrow field="rational" type="op-number"><mi>t</mi></mrow><mrow class="column-vector" field="op-number" type="vector">
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">-236</mn></mrow><mrow><mn field="rational" type="number">57</mn></mrow></mfrac></mrow></mrow>
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">-43</mn></mrow><mrow><mn field="rational" type="number">39</mn></mrow></mfrac></mrow></mrow>
  <mrow field="rational" type="op-number"><mn field="rational" type="number">0</mn></mrow>
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">-70</mn></mrow><mrow><mn field="rational" type="number">57</mn></mrow></mfrac></mrow></mrow>
  <mrow field="rational" type="op-number"><mn field="rational" type="number">1</mn></mrow>
</mrow><mo>|</mo><mrow field="rational" type="op-number"><mi>s</mi></mrow><mo>,</mo><mrow field="rational" type="op-number"><mi>t</mi></mrow><mo>&#8712; &#8477;</mo><mo>}</mo></mrow></data></unit><unit name="subtask_2"><data name="n"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">5</mn></mrow></data><data name="m"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">2</mn></mrow></data><data name="vector_0"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-number" type="vector">
  <mrow field="real" type="op-number"><mn field="real" type="number">-1</mn></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
</mrow></data><data name="vector_1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-number" type="vector">
  <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">-236</mn></mrow><mrow><mn field="real" type="number">57</mn></mrow></mfrac></mrow></mrow>
  <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">-43</mn></mrow><mrow><mn field="real" type="number">39</mn></mrow></mfrac></mrow></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
  <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">-70</mn></mrow><mrow><mn field="real" type="number">57</mn></mrow></mfrac></mrow></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
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
       WHERE pure_name='wks_determinante_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_determinante'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/determinante'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_determinante_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_determinante'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/determinante')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrix_1">
        <data name="component_11">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
        </data>
        <data name="component_12">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
        </data>
        <data name="component_13">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
        </data>
        <data name="component_14">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
        </data>
        <data name="component_22">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
        </data>
        <data name="component_23">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
        </data>
        <data name="component_24">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
        </data>
        <data name="component_33">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
        </data>
        <data name="component_34">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
        </data>
        <data name="component_44">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-3</mn>
        </data>
      </unit>
      <unit name="matrix_2">
        <data name="component_11">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
        </data>
        <data name="component_12">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
        </data>
        <data name="component_13">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">5</mn>
        </data>
        <data name="component_14">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
        </data>
        <data name="component_21">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
        </data>
        <data name="component_23">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
        </data>
        <data name="component_31">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
        </data>
        <data name="component_32">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
        </data>
        <data name="component_33">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
        </data>
        <data name="component_34">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-4</mn>
        </data>
        <data name="component_41">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-3</mn>
        </data>
        <data name="component_42">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-4</mn>
        </data>
        <data name="component_43">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
        </data>
        <data name="component_44">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
        </data>
      </unit>
      <unit name="matrix_3">
        <data name="component_12">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">4</mn>
        </data>
        <data name="component_13">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">6</mn>
        </data>
        <data name="component_23">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
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
       WHERE pure_name='wks_determinante_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_determinante_geometrie'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/determinante'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_determinante_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

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
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
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
       WHERE pure_name='wks_determinante_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_klausuraufgabe_determinante2'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/determinante'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_determinante_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_klausuraufgabe_determinante2'
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
   (SELECT id FROM users WHERE login_name = '@login-name@'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_determinante_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_kern_matrix-abbildung'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_determinante_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_kern_matrix-abbildung'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrix">
        <data name="coefficient_11">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
        </data>
        <data name="coefficient_12">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
        </data>
        <data name="coefficient_14">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
        </data>
        <data name="coefficient_15">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
        </data>
        <data name="coefficient_21">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">4</mn>
        </data>
        <data name="coefficient_22">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
        </data>
        <data name="coefficient_24">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
        </data>
        <data name="coefficient_25">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
        </data>
        <data name="coefficient_31">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
        </data>
        <data name="coefficient_32">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
        </data>
        <data name="coefficient_34">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
        </data>
        <data name="coefficient_35">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
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
       WHERE pure_name='wks_determinante_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_determinante1'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/determinante'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_determinante_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_determinante1'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/determinante')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="matrix1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">5</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
</mtable></data><data name="matrix2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">3</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">2</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_determinante_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_determinante2'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/determinante'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_determinante_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_determinante2'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/determinante')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="det1"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">0</mn></mrow></data><data name="det2"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">8</mn></mrow></data><data name="det3"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">-5</mn></mrow></data></unit><unit name="subtask_2"><data name="usermatrix1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="real" type="matrix">
  <mtr>
    <mtd>
      <mn field="real" type="number">-2</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">0</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="real" type="number">-4</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">-4</mn>
    </mtd>
  </mtr>
</mtable></data><data name="usermatrix2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="real" type="matrix">
  <mtr>
    <mtd>
      <mn field="real" type="number">-5</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">0</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="real" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">1</mn>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_determinante_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_determinante1'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/determinante'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_determinante_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_determinante1'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/determinante')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
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
       WHERE pure_name='wks_determinante_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_determinante2'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/determinante'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_determinante_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_determinante2'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/determinante')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrix_1">
        <data name="component_12">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
        </data>
        <data name="component_22">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">5</mn>
        </data>
      </unit>
      <unit name="matrix_2">
        <data name="component_11">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
        </data>
        <data name="component_12">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
        </data>
        <data name="component_21">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-4</mn>
        </data>
        <data name="component_22">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-4</mn>
        </data>
      </unit>
      <unit name="matrix_3">
        <data name="component_11">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
        </data>
        <data name="component_22">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
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
       WHERE pure_name='wks_diffgleichungen_1_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_exponential_funktion'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diffgleichungen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_diffgleichungen_1_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_exponential_funktion'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diffgleichungen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <data name="m">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
      </data>
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
       WHERE pure_name='wks_diffgleichungen_1_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_ldgs'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diffgleichungen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_diffgleichungen_1_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_ldgs'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diffgleichungen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="p_1">
        <data name="coefficient_1">
          <mfrac xmlns="http://www.w3.org/1998/Math/MathML" field="rational" type="number"><mn field="int" type="number">1</mn><mn field="int" type="number">6</mn></mfrac>
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
       WHERE pure_name='wks_diffgleichungen_2_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_dgs1'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diffgleichungen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_diffgleichungen_2_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_dgs1'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diffgleichungen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <data name="a">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
      </data>
      <data name="b">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
      </data>
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
       WHERE pure_name='wks_diffgleichungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_schwingung'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diffgleichungen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_diffgleichungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_schwingung'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diffgleichungen')),

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
       WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_charakteristisches-polynom'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/eigenvektoren-eigenwerte'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_charakteristisches-polynom'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/eigenvektoren-eigenwerte')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_2"><data name="poly2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="real" type="op-number"><mrow><mo>(</mo><mrow><mo>-</mo><mrow><mn field="real" type="number">6</mn><mo>*</mo><msup><mi>x</mi><mn field="real" type="number">3</mn></msup></mrow></mrow><mo>)</mo><mo>+</mo><mo>(</mo><mrow><mn field="real" type="number">7</mn><mo>*</mo><msup><mi>x</mi><mn field="real" type="number">2</mn></msup></mrow><mo>)</mo><mo>+</mo><mo>(</mo><mrow><mn field="real" type="number">4</mn><mo>*</mo><mi>x</mi></mrow><mo>)</mo><mo>+</mo><msup><mi>x</mi><mn field="real" type="number">4</mn></msup><mo>+</mo><mn field="real" type="number">6</mn></mrow></mrow></data></unit><unit name="subtask_1"><data name="poly1"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mrow><mo>(</mo><mrow><mn field="real" type="number">-2</mn><mo>*</mo><msup><mi>x</mi><mn field="real" type="number">2</mn></msup></mrow><mo>)</mo><mo>+</mo><msup><mi>x</mi><mn field="real" type="number">4</mn></msup><mo>+</mo><mn field="real" type="number">1</mn></mrow></mrow></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_eigenwerte-eigenvektoren'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/eigenvektoren-eigenwerte'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_eigenwerte-eigenvektoren'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/eigenvektoren-eigenwerte')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="p(z)"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="real" type="op-number"><mrow><msup><mi>z</mi><mn field="real" type="number">2</mn></msup><mo>-</mo><mo>(</mo><mi>z</mi><mo>)</mo><mo>+</mo><mn field="real" type="number">4</mn></mrow></mrow></data></unit><unit name="subtask_2"><data name="f"><mn xmlns="http://www.w3.org/1998/Math/MathML" field="real" type="number">0</mn></data><data name="z1"><cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" field="complex" type="number"><mn xmlns="http://www.w3.org/1998/Math/MathML" field="real" type="number">0</mn><mn xmlns="http://www.w3.org/1998/Math/MathML" field="real" type="number">2</mn></cnum></data><data name="z2"><cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" field="complex" type="number"><mn xmlns="http://www.w3.org/1998/Math/MathML" field="real" type="number">0</mn><mn xmlns="http://www.w3.org/1998/Math/MathML" field="real" type="number">-2</mn></cnum></data><data name="v1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="complex" type="vector">
  <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" field="complex" type="number"><mn xmlns="http://www.w3.org/1998/Math/MathML" field="real" type="number">0</mn><mn xmlns="http://www.w3.org/1998/Math/MathML" field="real" type="number">1</mn></cnum>
  <mn field="complex" type="number">1</mn>
</mrow></data><data name="v2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="complex" type="vector">
  <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" field="complex" type="number"><mn xmlns="http://www.w3.org/1998/Math/MathML" field="real" type="number">0</mn><mn xmlns="http://www.w3.org/1998/Math/MathML" field="real" type="number">-1</mn></cnum>
  <mn field="complex" type="number">1</mn>
</mrow></data></unit><unit name="subtask_3"><data name="f"><mn xmlns="http://www.w3.org/1998/Math/MathML" field="real" type="number">4</mn></data><data name="z"><mn xmlns="http://www.w3.org/1998/Math/MathML" field="complex" type="number">2</mn></data><data name="v"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="complex" type="vector">
  <mn field="complex" type="number">-1</mn>
  <mn field="complex" type="number">1</mn>
</mrow></data></unit><unit name="subtask_4"><data name="f"><mn field="real" type="number" xmlns="http://www.w3.org/1998/Math/MathML">5</mn></data><data name="z1"><mn field="complex" type="number" xmlns="http://www.w3.org/1998/Math/MathML">4</mn></data><data name="z2"><mn field="complex" type="number" xmlns="http://www.w3.org/1998/Math/MathML">1</mn></data><data name="v1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="complex" type="vector">
  <mn field="complex" type="number">-0,5</mn>
  <mn field="complex" type="number">1</mn>
</mrow></data><data name="v2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="complex" type="vector">
  <mn field="complex" type="number">-2</mn>
  <mn field="complex" type="number">1</mn>
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
       WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_lineare-abbildung-mit-eigenvektoren'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/eigenvektoren-eigenwerte'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_lineare-abbildung-mit-eigenvektoren'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/eigenvektoren-eigenwerte')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="real" type="matrix">
  <mtr>
    <mtd>
      <mn field="real" type="number">-3</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">2</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="real" type="number">2</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">-4</mn>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="real" type="matrix">
  <mtr>
    <mtd>
      <mn field="real" type="number">-1</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">0</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="real" type="number">-3</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">-1</mn>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_3"><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="real" type="matrix">
  <mtr>
    <mtd>
      <mn field="real" type="number">-1</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">0</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="real" type="number">-3</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">-1</mn>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_charakteristisches-polynom'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/eigenvektoren-eigenwerte'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_charakteristisches-polynom'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/eigenvektoren-eigenwerte')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficient_14">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
          </data>
          <data name="coefficient_24">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficient_34">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_44">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_14">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">4</mn>
          </data>
          <data name="coefficient_24">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
          </data>
          <data name="coefficient_34">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
          </data>
          <data name="coefficient_41">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_42">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficient_44">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
        </unit>
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
       WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_eigenwerte-eigenvektoren'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/eigenvektoren-eigenwerte'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_eigenwerte-eigenvektoren'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/eigenvektoren-eigenwerte')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <data name="omega">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
      </data>
      <data name="f">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
      </data>
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
       WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_klausuraufgabe03-02-19RT'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/eigenvektoren-eigenwerte'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_klausuraufgabe03-02-19RT'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/eigenvektoren-eigenwerte')),

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
       WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_lineare-abbildung-mit-eigenvektoren'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/eigenvektoren-eigenwerte'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_lineare-abbildung-mit-eigenvektoren'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/eigenvektoren-eigenwerte')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <data name="sum_1">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-3</mn>
      </data>
      <data name="sum_2">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
      </data>
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
       WHERE pure_name='wks_eigenvektoren-eigenwerte_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_charakteristisches-polynom'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/eigenvektoren-eigenwerte'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_eigenvektoren-eigenwerte_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_charakteristisches-polynom'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/eigenvektoren-eigenwerte')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="poly1"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mrow><msup><mi>x</mi><mn field="real" type="number">2</mn></msup><mo>+</mo><mi>x</mi><mo>+</mo><mn field="real" type="number">3</mn></mrow></mrow></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_eigenvektoren-eigenwerte_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_eigenwerte-eigenvektoren'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/eigenvektoren-eigenwerte'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_eigenvektoren-eigenwerte_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_eigenwerte-eigenvektoren'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/eigenvektoren-eigenwerte')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="x"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-number" type="vector">
  <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">-1</mn></mrow>
</mrow></data><data name="lambda"><mn field="real" type="number" xmlns="http://www.w3.org/1998/Math/MathML">2</mn></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_eigenvektoren-eigenwerte_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_charakteristisches-polynom'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/eigenvektoren-eigenwerte'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_eigenvektoren-eigenwerte_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_charakteristisches-polynom'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/eigenvektoren-eigenwerte')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-3</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
          </data>
        </unit>
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
       WHERE pure_name='wks_eigenvektoren-eigenwerte_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_eigenwerte-eigenvektoren'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/eigenvektoren-eigenwerte'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_eigenvektoren-eigenwerte_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_eigenwerte-eigenvektoren'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/eigenvektoren-eigenwerte')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrix">
        <data name="component_11">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
        </data>
        <data name="component_12">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-3</mn>
        </data>
        <data name="component_22">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
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
       WHERE pure_name='wks_euklidische_unitaere_vektorraeume_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_abstand_polynom'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/euklidische_unitaere_vektorraeume'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_euklidische_unitaere_vektorraeume_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_abstand_polynom'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/euklidische_unitaere_vektorraeume')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="d12"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mrow><mfrac><mrow><mn field="real" type="number">536</mn></mrow><mrow><mn field="real" type="number">15</mn></mrow></mfrac></mrow></mrow></data><data name="d23"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mrow><mfrac><mrow><mn field="real" type="number">206</mn></mrow><mrow><mn field="real" type="number">15</mn></mrow></mfrac></mrow></mrow></data><data name="d13"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mrow><mfrac><mrow><mn field="real" type="number">38</mn></mrow><mrow><mn field="real" type="number">3</mn></mrow></mfrac></mrow></mrow></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_euklidische_unitaere_vektorraeume_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_fusspunkt'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/euklidische_unitaere_vektorraeume'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_euklidische_unitaere_vektorraeume_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_fusspunkt'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/euklidische_unitaere_vektorraeume')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="ortsVector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector">
  <mrow field="real" type="op-number"><mn field="real" type="number">4</mn></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">17</mn></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">10</mn></mrow>
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
       WHERE pure_name='wks_euklidische_unitaere_vektorraeume_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_schmidtsche_orthonormierung'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/euklidische_unitaere_vektorraeume'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_euklidische_unitaere_vektorraeume_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_schmidtsche_orthonormierung'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/euklidische_unitaere_vektorraeume')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="vector_w1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-rational" type="vector">
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">-3</mn></mrow><mrow><mn field="rational" type="number">5</mn></mrow></mfrac></mrow></mrow>
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">-4</mn></mrow><mrow><mn field="rational" type="number">5</mn></mrow></mfrac></mrow></mrow>
</mrow></data><data name="vector_l2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-rational" type="vector">
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">28</mn></mrow><mrow><mn field="rational" type="number">75</mn></mrow></mfrac></mrow></mrow>
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">-7</mn></mrow><mrow><mn field="rational" type="number">25</mn></mrow></mfrac></mrow></mrow>
</mrow></data><data name="vector_w2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-rational" type="vector">
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">4</mn></mrow><mrow><mn field="rational" type="number">5</mn></mrow></mfrac></mrow></mrow>
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">-3</mn></mrow><mrow><mn field="rational" type="number">5</mn></mrow></mfrac></mrow></mrow>
</mrow></data></unit><unit name="subtask_2"><data name="vector_w1b"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-rational" type="vector">
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">-1</mn></mrow><mrow><mn field="rational" type="number">3</mn></mrow></mfrac></mrow></mrow>
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">-2</mn></mrow><mrow><mn field="rational" type="number">3</mn></mrow></mfrac></mrow></mrow>
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">-2</mn></mrow><mrow><mn field="rational" type="number">3</mn></mrow></mfrac></mrow></mrow>
</mrow></data><data name="vector_l2b"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-rational" type="vector">
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">1</mn></mrow><mrow><mn field="rational" type="number">3</mn></mrow></mfrac></mrow></mrow>
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">1</mn></mrow><mrow><mn field="rational" type="number">6</mn></mrow></mfrac></mrow></mrow>
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">-2</mn></mrow><mrow><mn field="rational" type="number">6</mn></mrow></mfrac></mrow></mrow>
</mrow></data><data name="vector_w2b"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-rational" type="vector">
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">2</mn></mrow><mrow><mn field="rational" type="number">3</mn></mrow></mfrac></mrow></mrow>
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">1</mn></mrow><mrow><mn field="rational" type="number">3</mn></mrow></mfrac></mrow></mrow>
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">-2</mn></mrow><mrow><mn field="rational" type="number">3</mn></mrow></mfrac></mrow></mrow>
</mrow></data><data name="vector_l3b"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-rational" type="vector">
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">1</mn></mrow><mrow><mn field="rational" type="number">4</mn></mrow></mfrac></mrow></mrow>
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">-1</mn></mrow><mrow><mn field="rational" type="number">3</mn></mrow></mfrac></mrow></mrow>
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">1</mn></mrow><mrow><mn field="rational" type="number">6</mn></mrow></mfrac></mrow></mrow>
</mrow></data><data name="vector_w3b"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-rational" type="vector">
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">3</mn></mrow><mrow><msqrt><mn field="rational" type="number">29</mn></msqrt></mrow></mfrac></mrow></mrow>
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">-4</mn></mrow><mrow><msqrt><mn field="rational" type="number">29</mn></msqrt></mrow></mfrac></mrow></mrow>
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">2</mn></mrow><mrow><msqrt><mn field="rational" type="number">29</mn></msqrt></mrow></mfrac></mrow></mrow>
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
       WHERE pure_name='wks_euklidische_unitaere_vektorraeume_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_abstand_polynom'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/euklidische_unitaere_vektorraeume'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_euklidische_unitaere_vektorraeume_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_abstand_polynom'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/euklidische_unitaere_vektorraeume')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="p_1">
        <data name="coefficient_0">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-4</mn>
        </data>
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-3</mn>
        </data>
      </unit>
      <unit name="p_2">
        <data name="coefficient_0">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
        </data>
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">4</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
        </data>
      </unit>
      <unit name="p_3">
        <data name="coefficient_0">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-3</mn>
        </data>
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">4</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-3</mn>
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
       WHERE pure_name='wks_euklidische_unitaere_vektorraeume_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_fusspunkt'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/euklidische_unitaere_vektorraeume'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_euklidische_unitaere_vektorraeume_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_fusspunkt'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/euklidische_unitaere_vektorraeume')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector_p">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-3</mn>
        </data>
        <data name="component_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
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
       WHERE pure_name='wks_euklidische_unitaere_vektorraeume_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_schmidtsche_orthonormierung'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/euklidische_unitaere_vektorraeume'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_euklidische_unitaere_vektorraeume_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_schmidtsche_orthonormierung'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/euklidische_unitaere_vektorraeume')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <data name="alpha">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-9</mn>
      </data>
      <data name="beta">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">6</mn>
      </data>
      <data name="gamma">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">8</mn>
      </data>
      <data name="delta">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
      </data>
      <data name="epsilon">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">6</mn>
      </data>
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
       WHERE pure_name='wks_euklidische_unitaere_vektorraeume_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_senkrechte_vektoren'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/euklidische_unitaere_vektorraeume'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_euklidische_unitaere_vektorraeume_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_senkrechte_vektoren'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/euklidische_unitaere_vektorraeume')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="x"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="rational" type="vector">
  <mn field="rational" type="number">3</mn>
  <mn field="rational" type="number">-2</mn>
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
       WHERE pure_name='wks_euklidische_unitaere_vektorraeume_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_standardskalarprodukt'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/euklidische_unitaere_vektorraeume'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_euklidische_unitaere_vektorraeume_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_standardskalarprodukt'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/euklidische_unitaere_vektorraeume')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="dotproduct"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mrow><mfrac><mrow><mn field="real" type="number">10</mn></mrow><mrow><mn field="real" type="number">3</mn></mrow></mfrac></mrow></mrow></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_euklidische_unitaere_vektorraeume_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_senkrechte_vektoren'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/euklidische_unitaere_vektorraeume'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_euklidische_unitaere_vektorraeume_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_senkrechte_vektoren'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/euklidische_unitaere_vektorraeume')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector_a">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="rational" type="number">-2</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="rational" type="number">-3</mn>
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
       WHERE pure_name='wks_euklidische_unitaere_vektorraeume_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_standardskalarprodukt'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/euklidische_unitaere_vektorraeume'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_euklidische_unitaere_vektorraeume_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_standardskalarprodukt'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/euklidische_unitaere_vektorraeume')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector_a">
        <data name="component_1">
          <mfrac xmlns="http://www.w3.org/1998/Math/MathML" field="rational" type="number"><mn field="int" type="number">-2</mn><mn field="int" type="number">3</mn></mfrac>
        </data>
        <data name="component_2">
          <mfrac xmlns="http://www.w3.org/1998/Math/MathML" field="rational" type="number"><mn field="int" type="number">1</mn><mn field="int" type="number">2</mn></mfrac>
        </data>
      </unit>
      <unit name="vector_b">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="rational" type="number">-5</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="rational" type="number">0</mn>
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
       WHERE pure_name='wks_gauss_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_gauss_zsf_rang'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/gauss'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_gauss_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_gauss_zsf_rang'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/gauss')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="orgmatrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="rational" type="matrix">
  <mtr>
    <mtd>
      <mn field="rational" type="number">4</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">4</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-6</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">4</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">-4</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-4</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">6</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-4</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-2</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
  </mtr>
</mtable></data><data name="rank"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">2</mn></mrow></data></unit><unit name="subtask_2"><data name="orgmatrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="rational" type="matrix">
  <mtr>
    <mtd>
      <mn field="rational" type="number">4</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-4</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">4</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-4</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">-6</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">6</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-2</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">4</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-4</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
  </mtr>
</mtable></data><data name="rank"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">2</mn></mrow></data></unit><unit name="subtask_3"><data name="orgmatrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="rational" type="matrix">
  <mtr>
    <mtd>
      <mn field="rational" type="number">84</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-84</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">12</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">-84</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">84</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-12</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">12</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-12</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">4</mn>
    </mtd>
  </mtr>
</mtable></data><data name="rank"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">2</mn></mrow></data></unit><unit name="subtask_4"><data name="orgmatrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="rational" type="matrix">
  <mtr>
    <mtd>
      <mn field="rational" type="number">32</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">32</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-48</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">32</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">32</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">32</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-48</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">32</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">-48</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-48</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">68</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-48</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">32</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">32</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-48</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">32</mn>
    </mtd>
  </mtr>
</mtable></data><data name="rank"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">2</mn></mrow></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_gauss_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_inverse'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/gauss'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_gauss_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_inverse'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/gauss')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="a_matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="rational" type="matrix">
  <mtr>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">-4</mn><mn field="int" type="number">3</mn></mfrac>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-1</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">1</mn><mn field="int" type="number">3</mn></mfrac>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">-1</mn><mn field="int" type="number">8</mn></mfrac>
    </mtd>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">3</mn><mn field="int" type="number">2</mn></mfrac>
    </mtd>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">9</mn><mn field="int" type="number">8</mn></mfrac>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="b_matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="rational" type="matrix">
  <mtr>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">1</mn><mn field="int" type="number">7</mn></mfrac>
    </mtd>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">-6</mn><mn field="int" type="number">35</mn></mfrac>
    </mtd>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">1</mn><mn field="int" type="number">14</mn></mfrac>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">3</mn><mn field="int" type="number">70</mn></mfrac>
    </mtd>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">3</mn><mn field="int" type="number">350</mn></mfrac>
    </mtd>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">1</mn><mn field="int" type="number">14</mn></mfrac>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">-2</mn><mn field="int" type="number">35</mn></mfrac>
    </mtd>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">-2</mn><mn field="int" type="number">175</mn></mfrac>
    </mtd>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">1</mn><mn field="int" type="number">14</mn></mfrac>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_gauss_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_nzsf-loesungsmenge'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/gauss'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_gauss_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_nzsf-loesungsmenge'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/gauss')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="nzsf"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="rational" type="matrix">
  <mtr>
    <mtd>
      <mn field="rational" type="number">1</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">2</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">1</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">2</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">1</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_3"><data name="nzsf"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="rational" type="matrix">
  <mtr>
    <mtd>
      <mn field="rational" type="number">1</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-1</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">1</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">1</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">1</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">1</mn>
    </mtd>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">-1</mn><mn field="int" type="number">2</mn></mfrac>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_4"><data name="solutionSelection"><mn field="int" type="number" xmlns="http://www.w3.org/1998/Math/MathML">3</mn></data><data name="variablesCount"><mn field="int" type="number" xmlns="http://www.w3.org/1998/Math/MathML">1</mn></data><data name="variable_0"><mrow field="rational" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mi>x</mi></mrow></data><data name="vector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-rational" type="vector">
  <mrow field="rational" type="op-number"><mn field="rational" type="number">-1</mn></mrow>
  <mrow field="rational" type="op-number"><mi>x</mi></mrow>
  <mrow field="rational" type="op-number"><mrow><mo>(</mo><mrow><mo>-</mo><mi>x</mi></mrow><mo>)</mo><mo>+</mo><mn field="rational" type="number">1</mn></mrow></mrow>
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">-1</mn></mrow><mrow><mn field="rational" type="number">2</mn></mrow></mfrac></mrow></mrow>
</mrow></data></unit><unit name="subtask_2"><data name="solutionSelection"><mn field="int" type="number" xmlns="http://www.w3.org/1998/Math/MathML">3</mn></data><data name="variablesCount"><mn field="int" type="number" xmlns="http://www.w3.org/1998/Math/MathML">2</mn></data><data name="variable_0"><mrow field="rational" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mi>x</mi></mrow></data><data name="variable_1"><mrow field="rational" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mi>y</mi></mrow></data><data name="vector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-rational" type="vector">
  <mrow field="rational" type="op-number"><mrow><mn field="rational" type="number">-2</mn><mo>*</mo><mi>y</mi></mrow></mrow>
  <mrow field="rational" type="op-number"><mrow><mo>(</mo><mrow><mn field="rational" type="number">-2</mn><mo>*</mo><mi>y</mi></mrow><mo>)</mo><mo>+</mo><mn field="rational" type="number">1</mn></mrow></mrow>
  <mrow field="rational" type="op-number"><mi>x</mi></mrow>
  <mrow field="rational" type="op-number"><mi>y</mi></mrow>
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
       WHERE pure_name='wks_gauss_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_gauss_zsf_rang'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/gauss'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_gauss_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_gauss_zsf_rang'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/gauss')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_S">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">5</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-4</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
        </unit>
        <unit name="matrix_D">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
        </unit>
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
       WHERE pure_name='wks_gauss_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_inverse'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/gauss'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_gauss_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_inverse'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/gauss')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_1">
          <data name="coefficent_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-9</mn>
          </data>
          <data name="coefficent_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-8</mn>
          </data>
          <data name="coefficent_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
          </data>
          <data name="coefficent_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficent_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-4</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficent_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficent_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">8</mn>
          </data>
          <data name="coefficent_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-9</mn>
          </data>
          <data name="coefficent_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
          </data>
          <data name="coefficent_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">10</mn>
          </data>
          <data name="coefficent_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
          </data>
          <data name="coefficent_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">8</mn>
          </data>
          <data name="coefficent_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">6</mn>
          </data>
        </unit>
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
       WHERE pure_name='wks_gauss_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_klausuraufgabe-meerwasser-entsalzungOWL'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/gauss'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_gauss_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_klausuraufgabe-meerwasser-entsalzungOWL'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/gauss')),

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
       WHERE pure_name='wks_gauss_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_nzsf-loesungsmenge'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/gauss'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_gauss_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_nzsf-loesungsmenge'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/gauss')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrix_A1">
        <data name="switch">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
        </data>
        <unit name="matrix1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
          </data>
          <data name="coefficient_14">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
          <data name="coefficient_15">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_24">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
          <data name="coefficient_25">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
        </unit>
        <unit name="matrix2">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
        </unit>
      </unit>
      <unit name="matrix_A2">
        <data name="switch">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
        </data>
        <unit name="matrix1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_14">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
          <data name="coefficient_15">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
          <data name="coefficient_25">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
          <data name="coefficient_35">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
        </unit>
        <unit name="matrix2">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
        </unit>
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
       WHERE pure_name='wks_gauss_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_koeffizientenmatrix'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/gauss'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_gauss_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_koeffizientenmatrix'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/gauss')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="matrix_a"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="rational" type="matrix">
  <mtr>
    <mtd>
      <mn field="rational" type="number">4</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">16</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">13</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">7</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">1</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">12</mn>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="matrix_b"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="rational" type="matrix">
  <mtr>
    <mtd>
      <mn field="rational" type="number">4</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">16</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">13</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-5</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">7</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">1</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">12</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">9</mn>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_gauss_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_loesungsmenge_lgs'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/gauss'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_gauss_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_loesungsmenge_lgs'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/gauss')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="a_m1"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">2</mn></mrow></data><data name="a_b1"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">0</mn></mrow></data><data name="a_m2"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">-1</mn></mrow></data><data name="a_b2"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">1</mn></mrow></data><data name="a_matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-2</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
</mtable></data><data name="a_vectorb"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="b_m1"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">2</mn></mrow></data><data name="b_b1"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">2</mn></mrow></data><data name="b_m2"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">2</mn></mrow></data><data name="b_b2"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">2</mn></mrow></data><data name="b_matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-2</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-2</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
</mtable></data><data name="b_vectorb"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">2</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">2</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_3"><data name="c_m1"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">2</mn></mrow></data><data name="c_b1"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">3</mn></mrow></data><data name="c_m2"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">2</mn></mrow></data><data name="c_b2"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">0</mn></mrow></data><data name="c_matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-2</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-2</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
</mtable></data><data name="c_vectorb"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">3</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_gauss_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_koeffizientenmatrix'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/gauss'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_gauss_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_koeffizientenmatrix'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/gauss')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_2">
          <data name="coefficent_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">4</mn>
          </data>
          <data name="coefficent_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">16</mn>
          </data>
          <data name="coefficent_14">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">13</mn>
          </data>
          <data name="b_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
          </data>
          <data name="coefficent_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">7</mn>
          </data>
          <data name="coefficent_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficent_24">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">12</mn>
          </data>
          <data name="b_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">9</mn>
          </data>
        </unit>
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
       WHERE pure_name='wks_gauss_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_loesungsmenge_lgs'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/gauss'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_gauss_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_loesungsmenge_lgs'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/gauss')),

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
       WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_darstellende-matrix_drehmomentabbildung'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/koordinaten-abbildung_darstellende-matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_darstellende-matrix_drehmomentabbildung'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/koordinaten-abbildung_darstellende-matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="repMatrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="op-double" type="matrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mo>(</mo><mrow><mo>(</mo><mrow><mo>-</mo><msqrt><mn field="real" type="number">11</mn></msqrt></mrow><mo>)</mo><mo>-</mo><mo>(</mo><mn field="real" type="number">5</mn><mo>)</mo></mrow><mo>)</mo></mrow><mrow><mn field="real" type="number">3</mn></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-2</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mo>(</mo><mrow><msqrt><mn field="real" type="number">11</mn></msqrt><mo>+</mo><mn field="real" type="number">5</mn></mrow><mo>)</mo></mrow><mrow><mn field="real" type="number">3</mn></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mo>(</mo><mrow><mo>(</mo><mrow><mo>-</mo><msqrt><mn field="real" type="number">11</mn></msqrt></mrow><mo>)</mo><mo>+</mo><mn field="real" type="number">5</mn></mrow><mo>)</mo></mrow><mrow><mn field="real" type="number">3</mn></mrow></mfrac></mrow></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">2</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mo>(</mo><mrow><msqrt><mn field="real" type="number">11</mn></msqrt><mo>-</mo><mo>(</mo><mn field="real" type="number">5</mn><mo>)</mo></mrow><mo>)</mo></mrow><mrow><mn field="real" type="number">3</mn></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_darstellende-matrix_eulerableitung'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/koordinaten-abbildung_darstellende-matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_darstellende-matrix_eulerableitung'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/koordinaten-abbildung_darstellende-matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">3</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">2</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_koordinatenvektoren'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/koordinaten-abbildung_darstellende-matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_koordinatenvektoren'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/koordinaten-abbildung_darstellende-matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="coordsvector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector">
  <mrow field="real" type="op-number"><mn field="real" type="number">-1</mn></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
</mrow></data></unit><unit name="subtask_2"><data name="coordsvector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector">
  <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">-1</mn></mrow><mrow><mn field="real" type="number">6</mn></mrow></mfrac></mrow></mrow>
  <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">1</mn></mrow><mrow><mn field="real" type="number">6</mn></mrow></mfrac></mrow></mrow>
</mrow></data></unit><unit name="subtask_3"><data name="coordsvector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector">
  <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">1</mn></mrow><mrow><mn field="real" type="number">5</mn></mrow></mfrac></mrow></mrow>
  <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">-1</mn></mrow><mrow><mn field="real" type="number">10</mn></mrow></mfrac></mrow></mrow>
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
       WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_darstellende-matrix_drehmomentabbildung'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/koordinaten-abbildung_darstellende-matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_darstellende-matrix_drehmomentabbildung'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/koordinaten-abbildung_darstellende-matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="basis">
        <data name="type">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
        </data>
        <data name="coefficient">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">5</mn>
        </data>
      </unit>
      <unit name="vector_a">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
        </data>
        <data name="component_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
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
       WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_darstellende-matrix_eulerableitung'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/koordinaten-abbildung_darstellende-matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_darstellende-matrix_eulerableitung'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/koordinaten-abbildung_darstellende-matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="basis">
        <unit name="polynom_1">
          <data name="coefficint_0">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
          </data>
          <data name="coefficint_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
          </data>
          <data name="coefficint_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">4</mn>
          </data>
        </unit>
        <unit name="polynom_2">
          <data name="coefficint_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
          </data>
          <data name="coefficint_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
        </unit>
        <unit name="polynom_3">
          <data name="coefficint_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
        </unit>
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
       WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_klausuraufgabeWS2007'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/koordinaten-abbildung_darstellende-matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_klausuraufgabeWS2007'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/koordinaten-abbildung_darstellende-matrizen')),

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
       WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_koordinatenvektoren'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/koordinaten-abbildung_darstellende-matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_koordinatenvektoren'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/koordinaten-abbildung_darstellende-matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="rational" type="number">-1</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="rational" type="number">0</mn>
        </data>
      </unit>
      <unit name="basis_1">
        <data name="component_11">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
        </data>
        <data name="component_12">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
        </data>
        <data name="component_21">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-3</mn>
        </data>
        <data name="component_22">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
        </data>
      </unit>
      <unit name="basis_2">
        <data name="component_11">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-4</mn>
        </data>
        <data name="component_12">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
        </data>
        <data name="component_21">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
        </data>
        <data name="component_22">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">4</mn>
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
       WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_koordinatenvektoren_in_r2'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/koordinaten-abbildung_darstellende-matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_koordinatenvektoren_in_r2'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/koordinaten-abbildung_darstellende-matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="coordsvector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector">
  <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">-3</mn></mrow><mrow><mn field="real" type="number">4</mn></mrow></mfrac></mrow></mrow>
  <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">-1</mn></mrow><mrow><mn field="real" type="number">2</mn></mrow></mfrac></mrow></mrow>
</mrow></data></unit><unit name="subtask_2"><data name="coordsvector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector">
  <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">-3</mn></mrow><mrow><mn field="real" type="number">4</mn></mrow></mfrac></mrow></mrow>
  <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">1</mn></mrow><mrow><mn field="real" type="number">8</mn></mrow></mfrac></mrow></mrow>
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
       WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_koordinatenvektoren_polynomen'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/koordinaten-abbildung_darstellende-matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_koordinatenvektoren_polynomen'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/koordinaten-abbildung_darstellende-matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="vector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector">
  <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">1</mn></mrow><mrow><mn field="real" type="number">8</mn></mrow></mfrac></mrow></mrow>
  <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">7</mn></mrow><mrow><mn field="real" type="number">9</mn></mrow></mfrac></mrow></mrow>
  <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">-1</mn></mrow><mrow><mn field="real" type="number">8</mn></mrow></mfrac></mrow></mrow>
</mrow></data></unit><unit name="subtask_2"><data name="vector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector">
  <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
  <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">1</mn></mrow><mrow><mn field="real" type="number">8</mn></mrow></mfrac></mrow></mrow>
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
       WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_koordinatenvektoren_in_r2'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/koordinaten-abbildung_darstellende-matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_koordinatenvektoren_in_r2'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/koordinaten-abbildung_darstellende-matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector">
        <data name="component_1">
          <mfrac xmlns="http://www.w3.org/1998/Math/MathML" field="rational" type="number"><mn field="int" type="number">-3</mn><mn field="int" type="number">4</mn></mfrac>
        </data>
        <data name="component_2">
          <mfrac xmlns="http://www.w3.org/1998/Math/MathML" field="rational" type="number"><mn field="int" type="number">-1</mn><mn field="int" type="number">2</mn></mfrac>
        </data>
      </unit>
      <unit name="basis_1">
        <data name="component_11">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
        </data>
        <data name="component_12">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
        </data>
        <data name="component_21">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
        </data>
        <data name="component_22">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
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
       WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_koordinatenvektoren_polynomen'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/koordinaten-abbildung_darstellende-matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_koordinatenvektoren_polynomen'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/koordinaten-abbildung_darstellende-matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="polynom">
        <data name="coefficient_2">
          <mfrac xmlns="http://www.w3.org/1998/Math/MathML" field="rational" type="number"><mn field="int" type="number">-1</mn><mn field="int" type="number">8</mn></mfrac>
        </data>
        <data name="coefficient_1">
          <mfrac xmlns="http://www.w3.org/1998/Math/MathML" field="rational" type="number"><mn field="int" type="number">7</mn><mn field="int" type="number">9</mn></mfrac>
        </data>
        <data name="coefficient_0">
          <mfrac xmlns="http://www.w3.org/1998/Math/MathML" field="rational" type="number"><mn field="int" type="number">1</mn><mn field="int" type="number">8</mn></mfrac>
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
       WHERE pure_name='wks_lineare_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_inverse_abbildung'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/lin_abb'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_lineare_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_inverse_abbildung'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/lin_abb')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="inverse_map"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mi>k</mi></mrow><mrow><mn field="real" type="number">12</mn></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mi>l</mi></mrow><mrow><mn field="real" type="number">4</mn></mrow></mfrac></mrow></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mi>m</mi></mrow><mrow><mn field="real" type="number">9</mn></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mi>n</mi></mrow><mrow><mn field="real" type="number">8</mn></mrow></mfrac></mrow></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_lineare_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_komposition_von_abbildungen'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/lin_abb'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_lineare_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_komposition_von_abbildungen'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/lin_abb')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="vector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="rational" type="op-number"><mrow><mo>(</mo><mrow><mn field="rational" type="number">3</mn><mo>*</mo><msup><mi>x</mi><mn field="rational" type="number">2</mn></msup></mrow><mo>)</mo><mo>-</mo><mo>(</mo><mrow><mn field="rational" type="number">3</mn><mo>*</mo><mi>x</mi></mrow><mo>)</mo><mo>+</mo><mn field="rational" type="number">3</mn></mrow></mrow></data></unit><unit name="subtask_2"><data name="vector"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="op-rational" type="matrix">
  <mtr>
    <mtd>
      <mrow field="rational" type="op-number"><mn field="rational" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="rational" type="op-number"><mn field="rational" type="number">6</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="rational" type="op-number"><mn field="rational" type="number">2</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_3"><data name="selection"><mn field="int" type="number" xmlns="http://www.w3.org/1998/Math/MathML">2</mn></data><data name="selectedType"><mn field="int" type="number" xmlns="http://www.w3.org/1998/Math/MathML">6</mn></data><data name="vector"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="op-rational" type="matrix">
  <mtr>
    <mtd>
      <mrow field="rational" type="op-number"><mn field="rational" type="number">9</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="rational" type="op-number"><mn field="rational" type="number">-6</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="rational" type="op-number"><mn field="rational" type="number">3</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_lineare_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_lineare_abbildungen_konzepte1'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/lin_abb'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_lineare_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_lineare_abbildungen_konzepte1'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/lin_abb')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="matrix_A"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
</mtable></data><data name="matrix_B"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="matrix_A"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
</mtable></data><data name="matrix_B"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_3"><data name="matrix_A"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">1</mn></mrow><mrow><mn field="real" type="number">2</mn></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">1</mn></mrow><mrow><mn field="real" type="number">2</mn></mrow></mfrac></mrow></mrow>
    </mtd>
  </mtr>
</mtable></data><data name="matrix_B"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_4"><data name="matrix_A"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">1</mn></mrow><mrow><mn field="real" type="number">2</mn></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">1</mn></mrow><mrow><mn field="real" type="number">2</mn></mrow></mfrac></mrow></mrow>
    </mtd>
  </mtr>
</mtable></data><data name="matrix_B"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_5"><data name="matrix_A"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">1</mn></mrow><mrow><mn field="real" type="number">2</mn></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">1</mn></mrow><mrow><mn field="real" type="number">2</mn></mrow></mfrac></mrow></mrow>
    </mtd>
  </mtr>
</mtable></data><data name="matrix_B"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_lineare_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_inverse_abbildung'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/lin_abb'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_lineare_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_inverse_abbildung'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/lin_abb')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="coefficients">
        <data name="coefficient_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">12</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">4</mn>
        </data>
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">9</mn>
        </data>
        <data name="coefficient_0">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">8</mn>
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
       WHERE pure_name='wks_lineare_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_klausuraufgabe-lineare-abbildung'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/lin_abb'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_lineare_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_klausuraufgabe-lineare-abbildung'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/lin_abb')),

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
       WHERE pure_name='wks_lineare_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_komposition_von_abbildungen'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/lin_abb'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_lineare_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_komposition_von_abbildungen'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/lin_abb')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <data name="type">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
      </data>
      <data name="switch">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
      </data>
      <data name="coefficient1">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
      </data>
      <data name="coefficient2">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
      </data>
      <data name="coefficient3">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
      </data>
      <data name="coefficient4">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
      </data>
      <data name="p1">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
      </data>
      <data name="p2">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
      </data>
      <data name="q1">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
      </data>
      <data name="q2">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-3</mn>
      </data>
      <data name="r1">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
      </data>
      <data name="r2">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
      </data>
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
       WHERE pure_name='wks_lineare_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_lineare_abbildungen_konzepte1'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/lin_abb'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_lineare_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_lineare_abbildungen_konzepte1'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/lin_abb')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <data name="coefficient">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
      </data>
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
       WHERE pure_name='wks_lineare_abbildungen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_kern_bild_zeilenextraktion'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/lin_abb'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_lineare_abbildungen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_kern_bild_zeilenextraktion'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/lin_abb')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">11</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_lineare_abbildungen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_linearitaet_der_zeilenextraktion'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/lin_abb'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_lineare_abbildungen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_linearitaet_der_zeilenextraktion'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/lin_abb')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="m"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">1</mn></mrow></data><data name="n"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">3</mn></mrow></data><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_lineare_abbildungen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_kern_bild_zeilenextraktion'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/lin_abb'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_lineare_abbildungen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_kern_bild_zeilenextraktion'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/lin_abb')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="common">
    <unit name="problem">
      <data name="coefficient">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
      </data>
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
       WHERE pure_name='wks_lineare_abbildungen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_linearitaet_der_zeilenextraktion'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/lin_abb'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_lineare_abbildungen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_linearitaet_der_zeilenextraktion'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/lin_abb')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="common">
    <unit name="problem">
      <data name="coefficient">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
      </data>
      <data name="lambda">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">24</mn>
      </data>
    </unit>
  </unit>
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">5</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-3</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">5</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">4</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">4</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
          <data name="coefficient_41">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
          </data>
          <data name="coefficient_42">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">4</mn>
          </data>
          <data name="coefficient_43">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">5</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-4</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">5</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">5</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-4</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficient_41">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_42">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
          </data>
          <data name="coefficient_43">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
        </unit>
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
       WHERE pure_name='wks_matrizen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_linearkombination_von_matrizen'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_matrizen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_linearkombination_von_matrizen'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="matrixSum1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">7</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">10</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-14</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">30</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-23</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">11</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-6</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">8</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-13</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="matrixSum2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">15</mn></mrow><mrow><mn field="real" type="number">2</mn></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">12</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">15</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">-79</mn></mrow><mrow><mn field="real" type="number">4</mn></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-8</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_matrizen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_multiplikation_von_matrizen'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_matrizen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_multiplikation_von_matrizen'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="matrix_user"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-27</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-15</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">27</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">15</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_matrizen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_linearkombination_von_matrizen'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_matrizen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_linearkombination_von_matrizen'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">10</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">7</mn>
        </data>
        <data name="coefficient_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">10</mn>
        </data>
        <data name="coefficient_4">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">6</mn>
        </data>
        <data name="coefficient_5">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">9</mn>
        </data>
        <data name="coefficient_6">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">5</mn>
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
       WHERE pure_name='wks_matrizen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_multiplikation_von_matrizen'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_matrizen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_multiplikation_von_matrizen'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">10</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-3</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-9</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">3</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">0</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">0</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">9</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">5</mn>
          </data>
        </unit>
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
       WHERE pure_name='wks_orthogonale_unitaere_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_orthogonale_abbildung'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/orthogonale_unitaere_abbildungen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_orthogonale_unitaere_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_orthogonale_abbildung'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/orthogonale_unitaere_abbildungen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="op-number" type="matrix">
  <mtr>
    <mtd>
      <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">-3</mn></mrow><mrow><msqrt><mn field="rational" type="number">34</mn></msqrt></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow field="rational" type="op-number"><mn field="rational" type="number">-5</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">-5</mn></mrow><mrow><msqrt><mn field="rational" type="number">34</mn></msqrt></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow field="rational" type="op-number"><mn field="rational" type="number">3</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_orthogonale_unitaere_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_spiegelung'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/orthogonale_unitaere_abbildungen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_orthogonale_unitaere_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_spiegelung'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/orthogonale_unitaere_abbildungen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="op-number" type="matrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_orthogonale_unitaere_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_vektorprodukt'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/orthogonale_unitaere_abbildungen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_orthogonale_unitaere_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_vektorprodukt'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/orthogonale_unitaere_abbildungen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="l"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-number" type="vector">
  <mrow field="rational" type="op-number"><mn field="rational" type="number">0</mn></mrow>
  <mrow field="rational" type="op-number"><mn field="rational" type="number">105</mn></mrow>
  <mrow field="rational" type="op-number"><mn field="rational" type="number">0</mn></mrow>
</mrow></data></unit><unit name="subtask_2"><data name="L"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-number" type="vector">
  <mrow field="rational" type="op-number"><mn field="rational" type="number">0</mn></mrow>
  <mrow field="rational" type="op-number"><mn field="rational" type="number">-54100</mn></mrow>
  <mrow field="rational" type="op-number"><mn field="rational" type="number">0</mn></mrow>
</mrow></data></unit><unit name="subtask_3"><data name="m_max"><mrow field="rational" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mrow><mfrac><mrow><mn field="rational" type="number">10820</mn></mrow><mrow><mn field="rational" type="number">21</mn></mrow></mfrac></mrow></mrow></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_orthogonale_unitaere_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_orthogonale_abbildung'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/orthogonale_unitaere_abbildungen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_orthogonale_unitaere_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_orthogonale_abbildung'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/orthogonale_unitaere_abbildungen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrix">
        <data name="component_11">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-3</mn>
        </data>
        <data name="component_21">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
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
       WHERE pure_name='wks_orthogonale_unitaere_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_spiegelung'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/orthogonale_unitaere_abbildungen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_orthogonale_unitaere_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_spiegelung'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/orthogonale_unitaere_abbildungen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
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
       WHERE pure_name='wks_orthogonale_unitaere_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_vektorprodukt'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/orthogonale_unitaere_abbildungen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_orthogonale_unitaere_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_vektorprodukt'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/orthogonale_unitaere_abbildungen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <data name="M">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">541</mn>
      </data>
      <data name="D">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">10</mn>
      </data>
      <data name="L">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">21</mn>
      </data>
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
       WHERE pure_name='wks_orthogonale_unitaere_abbildungen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_spiegelung'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/orthogonale_unitaere_abbildungen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_orthogonale_unitaere_abbildungen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_spiegelung'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/orthogonale_unitaere_abbildungen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-1</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_orthogonale_unitaere_abbildungen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_drehung'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/orthogonale_unitaere_abbildungen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_orthogonale_unitaere_abbildungen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_drehung'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/orthogonale_unitaere_abbildungen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <data name="angle">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
      </data>
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
       WHERE pure_name='wks_orthogonale_unitaere_abbildungen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_spiegelung'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/orthogonale_unitaere_abbildungen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_orthogonale_unitaere_abbildungen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_spiegelung'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/orthogonale_unitaere_abbildungen')),

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
       WHERE pure_name='wks_vektorraeume_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_basis_von_matrizen'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_basis_von_matrizen'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="selected_1">common/problem/matrices/matrix_3</data><data name="selected_2">common/problem/matrices/matrix_2</data><data name="selected_3">common/problem/matrices/matrix_1</data><data name="selected_4">common/problem/matrices/matrix_9</data><data name="nrOfElements"><mn field="int" type="number" xmlns="http://www.w3.org/1998/Math/MathML">4</mn></data></unit><unit name="subtask_2"><data name="selected_1">common/problem/matrices/matrix_5</data><data name="selected_2">common/problem/matrices/matrix_4</data><data name="selected_3">common/problem/matrices/matrix_7</data><data name="selected_4">common/problem/matrices/matrix_8</data><data name="nrOfElements"><mn field="int" type="number" xmlns="http://www.w3.org/1998/Math/MathML">4</mn></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_vektorraeume_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_koordinaten_basen_in_3-dim_VR'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_koordinaten_basen_in_3-dim_VR'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="coordVector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-rational" type="vector">
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">-190</mn></mrow><mrow><mn field="rational" type="number">189</mn></mrow></mfrac></mrow></mrow>
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">-8</mn></mrow><mrow><mn field="rational" type="number">27</mn></mrow></mfrac></mrow></mrow>
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">10</mn></mrow><mrow><mn field="rational" type="number">189</mn></mrow></mfrac></mrow></mrow>
</mrow></data></unit><unit name="subtask_2"><data name="coordVector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-rational" type="vector">
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">-1</mn></mrow><mrow><mn field="rational" type="number">2</mn></mrow></mfrac></mrow></mrow>
  <mrow field="rational" type="op-number"><mn field="rational" type="number">3</mn></mrow>
  <mrow field="rational" type="op-number"><mn field="rational" type="number">-1</mn></mrow>
</mrow></data></unit><unit name="subtask_3"><data name="coordVector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-rational" type="vector">
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">1</mn></mrow><mrow><mn field="rational" type="number">2</mn></mrow></mfrac></mrow></mrow>
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">-1</mn></mrow><mrow><mn field="rational" type="number">2</mn></mrow></mfrac></mrow></mrow>
  <mrow field="rational" type="op-number"><mrow><mfrac><mrow><mn field="rational" type="number">1</mn></mrow><mrow><mn field="rational" type="number">6</mn></mrow></mfrac></mrow></mrow>
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
       WHERE pure_name='wks_vektorraeume_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_lineare_huelle'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_lineare_huelle'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="selected_1">common/problem/polynom_8</data><data name="selected_2">common/problem/polynom_2</data><data name="selected_3">common/problem/polynom_4</data><data name="nrOfElements"><mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn></data></unit><unit name="subtask_2"><data name="selected_1">common/problem/polynom_4</data><data name="selected_2">common/problem/polynom_5</data><data name="selected_3">common/problem/polynom_9</data><data name="nrOfElements"><mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn></data></unit><unit name="subtask_3"><data name="selected_1">common/problem/polynom_1</data><data name="selected_2">common/problem/polynom_4</data><data name="selected_3">common/problem/polynom_7</data><data name="nrOfElements"><mn field="int" type="number" xmlns="http://www.w3.org/1998/Math/MathML">3</mn></data></unit><unit name="subtask_4"><data name="selected_1">common/problem/polynom_1</data><data name="selected_2">common/problem/polynom_3</data><data name="selected_3">common/problem/polynom_5</data><data name="nrOfElements"><mn field="int" type="number" xmlns="http://www.w3.org/1998/Math/MathML">3</mn></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_vektorraeume_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_basis_von_matrizen'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_basis_von_matrizen'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">5</mn>
          </data>
        </unit>
        <unit name="matrix_3">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">5</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">5</mn>
          </data>
        </unit>
        <unit name="matrix_4">
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-3</mn>
          </data>
        </unit>
        <unit name="matrix_6">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
        </unit>
        <unit name="matrix_7">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">5</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
        </unit>
        <unit name="matrix_8">
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
          </data>
        </unit>
        <unit name="matrix_9">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-3</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
          </data>
        </unit>
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
       WHERE pure_name='wks_vektorraeume_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_klausuraufgabe-teilraeume'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_klausuraufgabe-teilraeume'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume')),

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
       WHERE pure_name='wks_vektorraeume_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_koordinaten_basen_in_3-dim_VR'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_koordinaten_basen_in_3-dim_VR'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="basis_1">
        <data name="factor_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
        </data>
        <data name="factor_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
        </data>
        <unit name="vector_1">
          <data name="coefficient_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
          <data name="coefficient_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-3</mn>
          </data>
        </unit>
        <unit name="vector_2">
          <data name="coefficient_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
          </data>
          <data name="coefficient_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
          <data name="coefficient_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
        </unit>
      </unit>
      <unit name="vector_v">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
        </data>
        <data name="coefficient_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
        </data>
      </unit>
      <unit name="basis_2">
        <data name="factor_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
        </data>
        <data name="factor_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
        </data>
        <unit name="vector_1">
          <data name="coefficient_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
          <data name="coefficient_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
          </data>
          <data name="coefficient_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
        </unit>
        <unit name="vector_2">
          <data name="coefficient_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
          </data>
          <data name="coefficient_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficient_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
          </data>
        </unit>
      </unit>
      <unit name="vector_m">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-3</mn>
        </data>
        <data name="coefficient_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
        </data>
      </unit>
      <unit name="basis_3">
        <data name="factor_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
        </data>
        <data name="factor_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
        </data>
        <unit name="vector_1">
          <data name="coefficient_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
        </unit>
        <unit name="vector_2">
          <data name="coefficient_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
          </data>
          <data name="coefficient_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficient_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
        </unit>
      </unit>
      <unit name="vector_p">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
        </data>
        <data name="coefficient_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
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
       WHERE pure_name='wks_vektorraeume_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_lineare_huelle'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_lineare_huelle'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="polynom_1">
        <data name="coefficient_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
        </data>
        <data name="coefficient_0">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">5</mn>
        </data>
      </unit>
      <unit name="polynom_4">
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
        </data>
      </unit>
      <unit name="polynom_7">
        <data name="coefficient_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
        </data>
        <data name="coefficient_0">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">5</mn>
        </data>
      </unit>
      <unit name="polynom_2">
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-4</mn>
        </data>
      </unit>
      <unit name="polynom_8">
        <data name="coefficient_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
        </data>
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">4</mn>
        </data>
      </unit>
      <unit name="polynom_3">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-4</mn>
        </data>
        <data name="coefficient_0">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
        </data>
      </unit>
      <unit name="polynom_6">
        <data name="coefficient_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
        </data>
      </unit>
      <unit name="polynom_9">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
        </data>
        <data name="coefficient_0">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
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
       WHERE pure_name='wks_vektorraeume_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_erzeugenden-system'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_erzeugenden-system'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="selected_1">common/problem/polynom_1</data><data name="selected_2">common/problem/polynom_2</data><data name="selected_3">common/problem/polynom_4</data><data name="nrOfElements"><mn field="int" type="number" xmlns="http://www.w3.org/1998/Math/MathML">3</mn></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_vektorraeume_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_koordinaten_und_basen'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_koordinaten_und_basen'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="coordsB1vector1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector">
  <mrow field="real" type="op-number"><mn field="real" type="number">-1</mn></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">2</mn></mrow>
</mrow></data><data name="vector_1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="rational" type="vector">
  <mn field="rational" type="number">1</mn>
  <mn field="rational" type="number">-3</mn>
</mrow></data></unit><unit name="subtask_2"><data name="coordsB2vector1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector">
  <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">-3</mn></mrow><mrow><mn field="real" type="number">2</mn></mrow></mfrac></mrow></mrow>
  <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">7</mn></mrow><mrow><mn field="real" type="number">4</mn></mrow></mfrac></mrow></mrow>
</mrow></data><data name="vector_1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="rational" type="vector">
  <mn field="rational" type="number">1</mn>
  <mn field="rational" type="number">-3</mn>
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
       WHERE pure_name='wks_vektorraeume_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_erzeugenden-system'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_erzeugenden-system'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="polynom_1">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
        </data>
        <data name="coefficient_0">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">6</mn>
        </data>
      </unit>
      <unit name="polynom_2">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
        </data>
      </unit>
      <unit name="polynom_4">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">5</mn>
        </data>
      </unit>
      <unit name="polynom_6">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
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
       WHERE pure_name='wks_vektorraeume_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_koordinaten_und_basen'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_koordinaten_und_basen'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/vektorraeume')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector_1">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-3</mn>
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
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="selected_1">common/problem/vectors/vector_1</data><data name="selected_2">common/problem/vectors/vector_2</data><data name="selected_3">common/problem/vectors/vector_3</data></unit><unit name="subtask_2"><data name="selected_1">common/problem/vectors/vector_4</data><data name="selected_2">common/problem/vectors/vector_7</data><data name="selected_3">common/problem/vectors/vector_8</data></unit><unit name="subtask_3"><data name="selected_1">common/problem/vectors/vector_11</data><data name="selected_2">common/problem/vectors/vector_8</data><data name="selected_3">common/problem/vectors/vector_7</data></unit><unit name="subtask_4"><data name="selected_1">common/problem/vectors/vector_6</data><data name="selected_2">common/problem/vectors/vector_2</data><data name="selected_3">common/problem/vectors/vector_3</data></unit></unit></unit></data_sheet>');

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
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="coeff1"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">-1</mn></mrow></data><data name="coeff2"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">1</mn></mrow></data><data name="coeff3"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">3</mn></mrow></data></unit></unit></unit></data_sheet>');

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
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="vsum1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="complex-rational" type="vector">
  <mn field="complex-rational" type="number">5</mn>
  <mfrac field="complex-rational" type="number"><mn field="int" type="number">-13</mn><mn field="int" type="number">4</mn></mfrac>
</mrow></data></unit><unit name="subtask_2"><data name="vsum2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="complex-rational" type="vector">
  <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" field="complex-rational" type="number"><mn field="rational" type="number" xmlns="http://www.w3.org/1998/Math/MathML">7</mn><mn field="rational" type="number" xmlns="http://www.w3.org/1998/Math/MathML">-2</mn></cnum>
  <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" field="complex-rational" type="number"><mn field="rational" type="number" xmlns="http://www.w3.org/1998/Math/MathML">0</mn><mn field="rational" type="number" xmlns="http://www.w3.org/1998/Math/MathML">9</mn></cnum>
</mrow></data></unit><unit name="subtask_3"><data name="vsum3"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="complex-rational" type="vector">
  <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" field="complex-rational" type="number"><mn field="rational" type="number" xmlns="http://www.w3.org/1998/Math/MathML">2</mn><mn field="rational" type="number" xmlns="http://www.w3.org/1998/Math/MathML">7</mn></cnum>
  <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" field="complex-rational" type="number"><mn field="rational" type="number" xmlns="http://www.w3.org/1998/Math/MathML">6</mn><mn field="rational" type="number" xmlns="http://www.w3.org/1998/Math/MathML">-3</mn></cnum>
</mrow></data></unit><unit name="subtask_4"><data name="vsum4"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="complex-rational" type="vector">
  <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" field="complex-rational" type="number"><mn field="rational" type="number" xmlns="http://www.w3.org/1998/Math/MathML">6</mn><mn field="rational" type="number" xmlns="http://www.w3.org/1998/Math/MathML">3</mn></cnum>
  <mn field="complex-rational" type="number">6</mn>
</mrow></data></unit><unit name="subtask_5"><data name="vsum5"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="complex-rational" type="vector">
  <mn field="complex-rational" type="number">5</mn>
  <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" field="complex-rational" type="number"><mn field="rational" type="number" xmlns="http://www.w3.org/1998/Math/MathML">4</mn><mn field="rational" type="number" xmlns="http://www.w3.org/1998/Math/MathML">6</mn></cnum>
  <mn field="complex-rational" type="number">1</mn>
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
          <mn xmlns="http://www.w3.org/1998/Math/MathML">-2</mn>
        </data>
      </unit>
      <unit name="vector_2">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">-2</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
        </data>
      </unit>
      <unit name="vector_3">
        <data name="component_1">
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
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-4</mn>
          </data>
        </unit>
        <unit name="vector_2">
          <unit name="re-components">
            <data name="re-component_1">
              <mn xmlns="http://www.w3.org/1998/Math/MathML">3</mn>
            </data>
          </unit>
        </unit>
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
       WHERE pure_name='wks_diagonalisierbare_matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_housholder'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diagonalisierbare_matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_diagonalisierbare_matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_housholder'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diagonalisierbare_matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="matrixx"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="op-number" type="matrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="x1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-number" type="vector">
  <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
</mrow></data><data name="x2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-number" type="vector">
  <mrow field="real" type="op-number"><mn field="real" type="number">-1</mn></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
</mrow></data><data name="l1"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">1</mn></mrow></data><data name="l2"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">-1</mn></mrow></data></unit><unit name="subtask_3"><data name="repmatrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="op-number" type="matrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-1</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_diagonalisierbare_matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_markov'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diagonalisierbare_matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_diagonalisierbare_matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_markov'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diagonalisierbare_matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_2"><data name="lambda2"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mrow><mfrac><mrow><mn field="real" type="number">4</mn></mrow><mrow><mn field="real" type="number">7</mn></mrow></mfrac></mrow></mrow></data></unit><unit name="subtask_1"><data name="p"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-number" type="vector">
  <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">1</mn></mrow><mrow><mn field="real" type="number">3</mn></mrow></mfrac></mrow></mrow>
  <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">1</mn></mrow><mrow><mn field="real" type="number">3</mn></mrow></mfrac></mrow></mrow>
  <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">1</mn></mrow><mrow><mn field="real" type="number">3</mn></mrow></mfrac></mrow></mrow>
</mrow></data></unit><unit name="subtask_3"><data name="v1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-number" type="vector">
  <mrow field="real" type="op-number"><mn field="real" type="number">-1</mn></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
</mrow></data><data name="v2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-number" type="vector">
  <mrow field="real" type="op-number"><mn field="real" type="number">-1</mn></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
</mrow></data></unit><unit name="subtask_4"><data name="matrixS"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="op-number" type="matrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
</mtable></data><data name="matrixD"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="op-number" type="matrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">4</mn></mrow><mrow><mn field="real" type="number">7</mn></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">4</mn></mrow><mrow><mn field="real" type="number">7</mn></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_diagonalisierbare_matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_traegheitstensor'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diagonalisierbare_matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_diagonalisierbare_matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_traegheitstensor'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diagonalisierbare_matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="l1"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">0</mn></mrow></data><data name="l2"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">1340</mn></mrow></data><data name="v1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-number" type="vector">
  <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">1</mn></mrow><mrow><mn field="real" type="number">5</mn></mrow></mfrac></mrow></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
</mrow></data><data name="v2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-number" type="vector">
  <mrow field="real" type="op-number"><mn field="real" type="number">-5</mn></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
</mrow></data></unit><unit name="subtask_2"><data name="matrixS"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="op-number" type="matrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mrow><mfrac><mrow><mn field="real" type="number">1</mn></mrow><mrow><mn field="real" type="number">5</mn></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-5</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
</mtable></data><data name="matrixD"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="op-number" type="matrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1430</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_diagonalisierbare_matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_housholder'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diagonalisierbare_matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_diagonalisierbare_matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_housholder'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diagonalisierbare_matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector_1">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
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
       WHERE pure_name='wks_diagonalisierbare_matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_klausuraufgabe06-10-11RT'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diagonalisierbare_matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_diagonalisierbare_matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_klausuraufgabe06-10-11RT'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diagonalisierbare_matrizen')),

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
       WHERE pure_name='wks_diagonalisierbare_matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_markov'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diagonalisierbare_matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_diagonalisierbare_matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_markov'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diagonalisierbare_matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <data name="q">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">7</mn>
      </data>
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
       WHERE pure_name='wks_diagonalisierbare_matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_traegheitstensor'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diagonalisierbare_matrizen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_diagonalisierbare_matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_traegheitstensor'
    AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/diagonalisierbare_matrizen')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector_x">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">5</mn>
        </data>
      </unit>
    </unit>
  </unit>
</data_sheet>
');
