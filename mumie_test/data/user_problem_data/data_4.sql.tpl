
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
  <mn field="rational" type="number">-1</mn>
  <mfrac field="rational" type="number"><mn field="int" type="number">15</mn><mn field="int" type="number">4</mn></mfrac>
</mrow></data></unit><unit name="subtask_2"><data name="vec2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="rational" type="vector">
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
            <mn xmlns="http://www.w3.org/1998/Math/MathML">9</mn>
          </data>
        </unit>
        <unit name="vector_2">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-10</mn>
          </data>
        </unit>
      </unit>
      <unit name="subtask_2">
        <unit name="vector_1">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-3</mn>
          </data>
        </unit>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">-3</mn>
        </data>
        <unit name="vector_2">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-1</mn>
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
      <mn field="rational" type="number">2</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-1</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">3</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">1</mn>
    </mtd>
  </mtr>
</mtable></data><data name="a_v1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="rational" type="vector">
  <mn field="rational" type="number">1</mn>
  <mn field="rational" type="number">0</mn>
</mrow></data><data name="a_v2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="rational" type="vector">
  <mn field="rational" type="number">0</mn>
  <mn field="rational" type="number">1</mn>
</mrow></data><data name="a_w1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="rational" type="vector">
  <mn field="rational" type="number">2</mn>
  <mn field="rational" type="number">3</mn>
</mrow></data><data name="a_w2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="rational" type="vector">
  <mn field="rational" type="number">-1</mn>
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
      <mn field="real" type="number">4</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">-4</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">6</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="real" type="number">9</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">-3</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">-7</mn>
    </mtd>
  </mtr>
</mtable></data><data name="selectedMatrix_1">common/problem/matrices/matrix_1</data><data name="matrix_2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="real" type="matrix">
  <mtr>
    <mtd>
      <mn field="real" type="number">1</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">8</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="real" type="number">6</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">-2</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="real" type="number">-9</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">1</mn>
    </mtd>
  </mtr>
</mtable></data><data name="selectedMatrix_2">common/problem/matrices/matrix_4</data><data name="matrix_sum"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-74</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">46</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">54</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">71</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_4"><data name="matrix_1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="real" type="matrix">
  <mtr>
    <mtd>
      <mn field="real" type="number">-2</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">-5</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="real" type="number">5</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">2</mn>
    </mtd>
  </mtr>
</mtable></data><data name="selectedMatrix_1">common/problem/matrices/matrix_3</data><data name="matrix_2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="real" type="matrix">
  <mtr>
    <mtd>
      <mn field="real" type="number">-8</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">-8</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">-9</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="real" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">-7</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">7</mn>
    </mtd>
  </mtr>
</mtable></data><data name="selectedMatrix_2">common/problem/matrices/matrix_5</data><data name="matrix_sum"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">16</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">51</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-40</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-54</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_3"><data name="matrix_1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="real" type="matrix">
  <mtr>
    <mtd>
      <mn field="real" type="number">1</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">4</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="real" type="number">7</mn>
    </mtd>
    <mtd>
      <mn field="real" type="number">3</mn>
    </mtd>
  </mtr>
</mtable></data><data name="selectedMatrix_1">common/problem/matrices/matrix_2</data><data name="matrix_2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="real" type="matrix">
  <mtr>
    <mtd>
      <mn field="real" type="number">-7</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="real" type="number">1</mn>
    </mtd>
  </mtr>
</mtable></data><data name="selectedMatrix_2">common/problem/matrices/matrix_7</data><data name="matrix_sum"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-3</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-46</mn></mrow>
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
      <mrow field="real" type="op-number"><mn field="real" type="number">4</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">4</mn></mrow>
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
      <mrow field="real" type="op-number"><mn field="real" type="number">4</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">4</mn></mrow>
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
  <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
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
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
        </data>
      </unit>
      <unit name="vector_2">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-4</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
        </data>
      </unit>
      <unit name="vector_3">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
        </data>
        <data name="component_2">
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
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">4</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-4</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">6</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">9</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-3</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-7</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">4</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">7</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
          </data>
        </unit>
        <unit name="matrix_3">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">5</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
        </unit>
        <unit name="matrix_4">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">8</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">6</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-9</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
        </unit>
        <unit name="matrix_5">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-8</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-8</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-9</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-7</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">7</mn>
          </data>
        </unit>
        <unit name="matrix_6">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">4</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">9</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-7</mn>
          </data>
        </unit>
        <unit name="matrix_7">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-7</mn>
          </data>
          <data name="coefficient_21">
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
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
      </data>
      <data name="coefficient_2">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
      </data>
      <data name="coefficient_3">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">4</mn>
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
      <mn field="rational" type="number">-2</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-2</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-6</mn>
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
      <mn field="rational" type="number">4</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">6</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">4</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">2</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">2</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">4</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">2</mn>
    </mtd>
  </mtr>
</mtable></data><data name="rank"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">2</mn></mrow></data></unit><unit name="subtask_2"><data name="orgmatrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="rational" type="matrix">
  <mtr>
    <mtd>
      <mn field="rational" type="number">2</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">4</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">2</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">-2</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">4</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">2</mn>
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
      <mn field="rational" type="number">4</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">-2</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">4</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">2</mn>
    </mtd>
  </mtr>
</mtable></data><data name="rank"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">3</mn></mrow></data></unit><unit name="subtask_3"><data name="orgmatrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="rational" type="matrix">
  <mtr>
    <mtd>
      <mn field="rational" type="number">48</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-60</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-36</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">-60</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">84</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">48</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">-36</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">48</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">28</mn>
    </mtd>
  </mtr>
</mtable></data><data name="rank"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">2</mn></mrow></data></unit><unit name="subtask_4"><data name="orgmatrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="rational" type="matrix">
  <mtr>
    <mtd>
      <mn field="rational" type="number">24</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">24</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">44</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">24</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">24</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">24</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">44</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">24</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">44</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">44</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">88</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">44</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">24</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">24</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">44</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">24</mn>
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
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="a_matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="rational" type="matrix">
  <mtr>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">1</mn><mn field="int" type="number">24</mn></mfrac>
    </mtd>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">1</mn><mn field="int" type="number">9</mn></mfrac>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">1</mn><mn field="int" type="number">8</mn></mfrac>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">1</mn><mn field="int" type="number">4</mn></mfrac>
    </mtd>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">1</mn><mn field="int" type="number">24</mn></mfrac>
    </mtd>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">1</mn><mn field="int" type="number">9</mn></mfrac>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="b_matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="rational" type="matrix">
  <mtr>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">47</mn><mn field="int" type="number">644</mn></mfrac>
    </mtd>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">-67</mn><mn field="int" type="number">644</mn></mfrac>
    </mtd>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">-39</mn><mn field="int" type="number">644</mn></mfrac>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">6</mn><mn field="int" type="number">161</mn></mfrac>
    </mtd>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">12</mn><mn field="int" type="number">161</mn></mfrac>
    </mtd>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">19</mn><mn field="int" type="number">161</mn></mfrac>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">-5</mn><mn field="int" type="number">161</mn></mfrac>
    </mtd>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">-10</mn><mn field="int" type="number">161</mn></mfrac>
    </mtd>
    <mtd>
      <mfrac field="rational" type="number"><mn field="int" type="number">11</mn><mn field="int" type="number">161</mn></mfrac>
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
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="nzsf"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="rational" type="matrix">
  <mtr>
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
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">2</mn>
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
      <mn field="rational" type="number">1</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">2</mn>
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
      <mn field="rational" type="number">0</mn>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="solutionSelection"><mn field="int" type="number" xmlns="http://www.w3.org/1998/Math/MathML">3</mn></data><data name="variablesCount"><mn field="int" type="number" xmlns="http://www.w3.org/1998/Math/MathML">0</mn></data><data name="vector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-rational" type="vector">
  <mrow field="rational" type="op-number"><mi>s</mi></mrow>
  <mrow field="rational" type="op-number"><mrow><mn field="rational" type="number">2</mn><mo>-</mo><mo>(</mo><mi>s</mi><mo>)</mo></mrow></mrow>
  <mrow field="rational" type="op-number"><mn field="rational" type="number">2</mn></mrow>
  <mrow field="rational" type="op-number"><mn field="rational" type="number">0</mn></mrow>
</mrow></data></unit><unit name="subtask_3"><data name="nzsf"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="rational" type="matrix">
  <mtr>
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
      <mn field="rational" type="number">0</mn>
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
      <mn field="rational" type="number">0</mn>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_4"><data name="solutionSelection"><mn field="int" type="number" xmlns="http://www.w3.org/1998/Math/MathML">3</mn></data><data name="variablesCount"><mn field="int" type="number" xmlns="http://www.w3.org/1998/Math/MathML">0</mn></data><data name="vector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-rational" type="vector">
  <mrow field="rational" type="op-number"><mi>s</mi></mrow>
  <mrow field="rational" type="op-number"><mrow><mo>-</mo><mi>s</mi></mrow></mrow>
  <mrow field="rational" type="op-number"><mn field="rational" type="number">2</mn></mrow>
  <mrow field="rational" type="op-number"><mn field="rational" type="number">0</mn></mrow>
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
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-4</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">5</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
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
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-4</mn>
          </data>
          <data name="coefficent_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">4</mn>
          </data>
          <data name="coefficent_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">8</mn>
          </data>
          <data name="coefficent_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">9</mn>
          </data>
          <data name="coefficent_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-3</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficent_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">8</mn>
          </data>
          <data name="coefficent_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">7</mn>
          </data>
          <data name="coefficent_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
          </data>
          <data name="coefficent_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-4</mn>
          </data>
          <data name="coefficent_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
          <data name="coefficent_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-7</mn>
          </data>
          <data name="coefficent_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">5</mn>
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
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
        </data>
        <unit name="matrix1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_14">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
          <data name="coefficient_15">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
          <data name="coefficient_24">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_25">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
        </unit>
        <unit name="matrix2">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
        </unit>
      </unit>
      <unit name="matrix_A2">
        <data name="switch">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
        </data>
        <unit name="matrix1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_14">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_15">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
          <data name="coefficient_25">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
          <data name="coefficient_35">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
        </unit>
        <unit name="matrix2">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_33">
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
      <mn field="rational" type="number">3</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">1</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">16</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">6</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">18</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">15</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">0</mn>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="matrix_b"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="rational" type="matrix">
  <mtr>
    <mtd>
      <mn field="rational" type="number">3</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">1</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">16</mn>
    </mtd>
    <mtd>
      <mi field="rational" type="number"/>
    </mtd>
    <mtd>
      <mn field="rational" type="number">-3</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn field="rational" type="number">6</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">18</mn>
    </mtd>
    <mtd>
      <mn field="rational" type="number">15</mn>
    </mtd>
    <mtd>
      <mi field="rational" type="number"/>
    </mtd>
    <mtd>
      <mn field="rational" type="number">6</mn>
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
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="a_m1"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">2</mn></mrow></data><data name="a_b1"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">0</mn></mrow></data><data name="a_m2"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">-1</mn></mrow></data><data name="a_b2"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">-3</mn></mrow></data><data name="a_matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
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
      <mrow field="real" type="op-number"><mn field="real" type="number">-3</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="b_m1"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">3</mn></mrow></data><data name="b_b1"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">2</mn></mrow></data><data name="b_m2"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">3</mn></mrow></data><data name="b_b2"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">2</mn></mrow></data><data name="b_matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-3</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-3</mn></mrow>
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
</mtable></data></unit><unit name="subtask_3"><data name="c_m1"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">2</mn></mrow></data><data name="c_b1"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">0</mn></mrow></data><data name="c_m2"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">2</mn></mrow></data><data name="c_b2"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">1</mn></mrow></data><data name="c_matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
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
      <mrow field="real" type="op-number"><mn field="real" type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
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
            <mn xmlns="http://www.w3.org/1998/Math/MathML">3</mn>
          </data>
          <data name="coefficent_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
          </data>
          <data name="coefficent_14">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">16</mn>
          </data>
          <data name="b_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-3</mn>
          </data>
          <data name="coefficent_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">6</mn>
          </data>
          <data name="coefficent_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">18</mn>
          </data>
          <data name="coefficent_24">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">15</mn>
          </data>
          <data name="b_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">6</mn>
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
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
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
      <mrow field="real" type="op-number"><mn field="real" type="number">12</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">21</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">12</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">22</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">13</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">31</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">1333</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">331</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">123</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-222</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-313</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-22</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">999</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">12</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">122</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">21</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">212</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">212</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">211</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">666</mn></mrow>
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
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="m"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">1</mn></mrow></data><data name="n"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">3</mn></mrow></data><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
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
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
      </data>
      <data name="lambda">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">21</mn>
      </data>
    </unit>
  </unit>
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">4</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-4</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-4</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-3</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-4</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-4</mn>
          </data>
          <data name="coefficient_41">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-3</mn>
          </data>
          <data name="coefficient_42">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
          </data>
          <data name="coefficient_43">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-3</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">0</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">2</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-5</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">1</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
          </data>
          <data name="coefficient_41">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-2</mn>
          </data>
          <data name="coefficient_42">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-3</mn>
          </data>
          <data name="coefficient_43">
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
      <mrow field="real" type="op-number"><mn field="real" type="number">-1</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">3</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-6</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">9</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-8</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">2</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-6</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">4</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">7</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="matrixSum2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix">
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">20</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">14</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">17</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">15</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-32,75</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">4</mn></mrow>
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
      <mrow field="real" type="op-number"><mn field="real" type="number">-66</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">33</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">-42</mn></mrow>
    </mtd>
    <mtd>
      <mrow field="real" type="op-number"><mn field="real" type="number">63</mn></mrow>
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
          <mn xmlns="http://www.w3.org/1998/Math/MathML">3</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">3</mn>
        </data>
        <data name="coefficient_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">5</mn>
        </data>
        <data name="coefficient_4">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">7</mn>
        </data>
        <data name="coefficient_5">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">2</mn>
        </data>
        <data name="coefficient_6">
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
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-5</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-6</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-7</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">0</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">6</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-9</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">6</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">2</mn>
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
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="selected_1">common/problem/polynom_1</data><data name="selected_2">common/problem/polynom_6</data><data name="selected_3">common/problem/polynom_4</data><data name="nrOfElements"><mn field="int" type="number" xmlns="http://www.w3.org/1998/Math/MathML">3</mn></data></unit></unit></unit></data_sheet>');

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
  <mrow field="real" type="op-number"><mn field="real" type="number">1,25</mn></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">-2,75</mn></mrow>
</mrow></data><data name="vector_1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="rational" type="vector">
  <mfrac field="rational" type="number"><mn field="int" type="number">-3</mn><mn field="int" type="number">2</mn></mfrac>
  <mn field="rational" type="number">4</mn>
</mrow></data></unit><unit name="subtask_2"><data name="coordsB2vector1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector">
  <mrow field="real" type="op-number"><mn field="real" type="number">2</mn></mrow>
  <mrow field="real" type="op-number"><mn field="real" type="number">-2,5</mn></mrow>
</mrow></data><data name="vector_1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="rational" type="vector">
  <mfrac field="rational" type="number"><mn field="int" type="number">-3</mn><mn field="int" type="number">2</mn></mfrac>
  <mn field="rational" type="number">4</mn>
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
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">5</mn>
        </data>
      </unit>
      <unit name="polynom_2">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">3</mn>
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
          <mn xmlns="http://www.w3.org/1998/Math/MathML" field="int" type="number">-1</mn>
        </data>
        <data name="coefficient_2">
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
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="selected_1">common/problem/vectors/vector_2</data><data name="selected_2">common/problem/vectors/vector_3</data><data name="selected_3">common/problem/vectors/vector_1</data></unit><unit name="subtask_2"><data name="selected_1">common/problem/vectors/vector_11</data><data name="selected_2">common/problem/vectors/vector_9</data><data name="selected_3">common/problem/vectors/vector_7</data></unit><unit name="subtask_3"><data name="selected_1">common/problem/vectors/vector_2</data><data name="selected_2">common/problem/vectors/vector_5</data><data name="selected_3">common/problem/vectors/vector_7</data></unit><unit name="subtask_4"><data name="selected_1">common/problem/vectors/vector_1</data><data name="selected_2">common/problem/vectors/vector_3</data><data name="selected_3">common/problem/vectors/vector_9</data></unit></unit></unit></data_sheet>');

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
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="coeff1"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">-3</mn></mrow></data><data name="coeff2"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn field="real" type="number">0</mn></mrow></data><data name="coeff3"><mrow field="real" type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mrow><mfrac><mrow><mn field="real" type="number">1</mn></mrow><mrow><mn field="real" type="number">4</mn></mrow></mfrac></mrow></mrow></data></unit></unit></unit></data_sheet>');

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
  <mn field="complex-rational" type="number">0</mn>
  <mfrac field="complex-rational" type="number"><mn field="int" type="number">3</mn><mn field="int" type="number">4</mn></mfrac>
</mrow></data></unit><unit name="subtask_2"><data name="vsum2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="complex-rational" type="vector">
  <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" field="complex-rational" type="number"><mn field="rational" type="number" xmlns="http://www.w3.org/1998/Math/MathML">7</mn><mn field="rational" type="number" xmlns="http://www.w3.org/1998/Math/MathML">-2</mn></cnum>
  <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" field="complex-rational" type="number"><mn field="rational" type="number" xmlns="http://www.w3.org/1998/Math/MathML">-6</mn><mn field="rational" type="number" xmlns="http://www.w3.org/1998/Math/MathML">3</mn></cnum>
</mrow></data></unit><unit name="subtask_3"><data name="vsum3"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="complex-rational" type="vector">
  <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" field="complex-rational" type="number"><mn field="rational" type="number" xmlns="http://www.w3.org/1998/Math/MathML">2</mn><mn field="rational" type="number" xmlns="http://www.w3.org/1998/Math/MathML">7</mn></cnum>
  <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" field="complex-rational" type="number"><mn field="rational" type="number" xmlns="http://www.w3.org/1998/Math/MathML">6</mn><mn field="rational" type="number" xmlns="http://www.w3.org/1998/Math/MathML">-3</mn></cnum>
</mrow></data></unit><unit name="subtask_4"><data name="vsum4"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="complex-rational" type="vector">
  <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" field="complex-rational" type="number"><mn field="rational" type="number" xmlns="http://www.w3.org/1998/Math/MathML">0</mn><mn field="rational" type="number" xmlns="http://www.w3.org/1998/Math/MathML">6</mn></cnum>
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
          <mn xmlns="http://www.w3.org/1998/Math/MathML">-1</mn>
        </data>
      </unit>
      <unit name="vector_2">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">-1</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
        </data>
      </unit>
      <unit name="vector_3">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">4</mn>
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
              <mn xmlns="http://www.w3.org/1998/Math/MathML">-2</mn>
            </data>
          </unit>
        </unit>
      </unit>
    </unit>
  </unit>
</data_sheet>
');
