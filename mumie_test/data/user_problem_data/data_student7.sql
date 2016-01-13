
INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_diffgleichungen_1_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_ldgs'
       AND contained_in=section_id_for_path(0, 'content/tub/differential_equations/problems/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_diffgleichungen_1_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_ldgs'
    AND contained_in=section_id_for_path(0, 'content/tub/differential_equations/problems/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="p_1">
        <data name="coefficient_1">
          <mfrac xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number"><mn mf:field="int" mf:type="number">1</mn><mn mf:field="int" mf:type="number">5</mn></mfrac>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_diffgleichungen_1_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_ldgs'
       AND contained_in=section_id_for_path(0, 'content/tub/differential_equations/problems/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_diffgleichungen_1_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_ldgs'
    AND contained_in=section_id_for_path(0, 'content/tub/differential_equations/problems/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">2</data></unit><unit name="subtask_1"><data name="a"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="a_{11} = " mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow></data><data name="b"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="a_{12} = " mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="real" mf:type="number">-1</mn></mrow><mrow><mn mf:field="real" mf:type="number">5</mn></mrow></mfrac></mrow></mrow></data><data name="c"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="a_{21} = " mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="real" mf:type="number">1</mn></mrow><mrow><mn mf:field="real" mf:type="number">5</mn></mrow></mfrac></mrow></mrow></data><data name="d"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="a_{22} = " mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow></data></unit><unit name="subtask_2"><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="A = " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="real" mf:type="number">-1</mn></mrow><mrow><mn mf:field="real" mf:type="number">5</mn></mrow></mfrac></mrow></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="real" mf:type="number">1</mn></mrow><mrow><mn mf:field="real" mf:type="number">5</mn></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_diffgleichungen_1_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_exponential_funktion'
       AND contained_in=section_id_for_path(0, 'content/tub/differential_equations/problems/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_diffgleichungen_1_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_exponential_funktion'
    AND contained_in=section_id_for_path(0, 'content/tub/differential_equations/problems/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <data name="m">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
      </data>
    </unit>
  </unit>
</data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_diffgleichungen_1_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_exponential_funktion'
       AND contained_in=section_id_for_path(0, 'content/tub/differential_equations/problems/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_diffgleichungen_1_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_exponential_funktion'
    AND contained_in=section_id_for_path(0, 'content/tub/differential_equations/problems/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">1</data></unit><unit name="subtask_2"><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="op-number" mf:label="e^{tM} = " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mrow><mn mf:field="real" mf:type="number">2</mn><mo>*</mo><mi>t</mi></mrow></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_1"><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="e^{tM} = " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-4</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-4</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');



INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_gauss_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_inverse'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_gauss_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_inverse'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_1">
          <data name="coefficent_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">10</mn>
          </data>
          <data name="coefficent_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficent_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">6</mn>
          </data>
          <data name="coefficent_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-8</mn>
          </data>
          <data name="coefficent_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-8</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficent_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">8</mn>
          </data>
          <data name="coefficent_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficent_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-4</mn>
          </data>
          <data name="coefficent_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-10</mn>
          </data>
          <data name="coefficent_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">8</mn>
          </data>
          <data name="coefficent_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-7</mn>
          </data>
          <data name="coefficent_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficent_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">8</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_gauss_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_nzsf-loesungsmenge'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_gauss_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_nzsf-loesungsmenge'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrix_A1">
        <data name="switch">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
        </data>
        <unit name="matrix1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_14">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="coefficient_15">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_24">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficient_25">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
        </unit>
        <unit name="matrix2">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
        </unit>
      </unit>
      <unit name="matrix_A2">
        <data name="switch">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
        </data>
        <unit name="matrix1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_14">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_15">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_25">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficient_35">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
        </unit>
        <unit name="matrix2">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_gauss_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_nzsf-loesungsmenge'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_gauss_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_nzsf-loesungsmenge'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">2</data></unit><unit name="subtask_3"><data name="nzsf"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="rational" mf:label="NZSF ( [A_2|\\vec{b_2}] ) = " mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="rational" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">4</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">-2</mn>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_4"><data name="solutionSelection"><mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:hidden="true" mf:type="number">3</mn></data><data name="dummySelection"><mtext xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="string" mf:type="text">Das lineare Gleichungssystem hat unendlich viele L&amp;ouml;sungen.</mtext></data><data name="set"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="tuple-set" mf:field="rational" mf:label="L = " mf:type="tuple-set"><mo>{</mo><mrow class="column-vector" mf:field="op-number" mf:type="vector">
  <mrow mf:field="rational" mf:type="op-number"><mn mf:field="rational" mf:type="number">4</mn></mrow>
  <mrow mf:field="rational" mf:type="op-number"><mrow><mn mf:field="int" mf:type="number">-1</mn><mo>*</mo><mi>s</mi></mrow></mrow>
  <mrow mf:field="rational" mf:type="op-number"><mi>s</mi></mrow>
  <mrow mf:field="rational" mf:type="op-number"><mn mf:field="rational" mf:type="number">-2</mn></mrow>
</mrow><mo>|</mo><mrow mf:field="rational" mf:type="op-number"><mi>s</mi></mrow><mo>&#8712; &#8477;</mo><mo>}</mo></mrow></data></unit><unit name="subtask_1"><data name="nzsf"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="rational" mf:label="NZSF ( [A_1|\\vec{b_1}] ) = " mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="rational" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">-1</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">1</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">2</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">-2</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="solutionSelection"><mn xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:hidden="true" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">1</mn></data><data name="dummySelection"><mtext xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="string" mf:type="text" xmlns="http://www.w3.org/1998/Math/MathML">Das lineare Gleichungssystem hat keine Loesung.</mtext></data><data name="set"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="tuple-set" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:label="L = " mf:type="tuple-set"><mo>{</mo><mo>}</mo></mrow></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_gauss_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_bild-kern_mc'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_gauss_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_bild-kern_mc'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrix">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_koordinatenabbildung'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_koordinatenabbildung'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <data name="coefficient">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-5</mn>
      </data>
    </unit>
  </unit>
</data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_koordinatenabbildung'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_koordinatenabbildung'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">1</data></unit><unit name="subtask_1"><data name="dimension"><mn xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:label="n =" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">2</mn></data><data name="vector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:type="vector">
  <mrow mf:field="real" mf:type="op-number"><mrow><mo>(</mo><mrow><mo>(</mo><mrow><mn mf:field="real" mf:type="number">4</mn><mo>*</mo><mi>a</mi></mrow><mo>)</mo><mo>-</mo><mo>(</mo><mrow><mn mf:field="real" mf:type="number">5</mn><mo>*</mo><mi>b</mi></mrow><mo>)</mo></mrow><mo>)</mo><mo>*</mo><mfrac><mn mf:field="int" mf:type="number">1</mn><mn mf:field="real" mf:type="number">5</mn></mfrac></mrow></mrow>
  <mrow mf:field="real" mf:type="op-number"><mrow><mo>(</mo><mrow><mo>-</mo><mi>a</mi></mrow><mo>)</mo><mo>+</mo><mi>b</mi></mrow></mrow>
</mrow></data></unit></unit></unit></data_sheet>');



INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_koordinatenvektoren'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_koordinatenvektoren'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector">
        <data name="component_1">
          <mfrac xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number"><mn mf:field="int" mf:type="number">-1</mn><mn mf:field="int" mf:type="number">2</mn></mfrac>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number">2</mn>
        </data>
      </unit>
      <unit name="basis_1">
        <data name="component_11">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
        </data>
        <data name="component_12">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
        <data name="component_21">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
        </data>
        <data name="component_22">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
        </data>
      </unit>
      <unit name="basis_2">
        <data name="component_11">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
        </data>
        <data name="component_12">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
        </data>
        <data name="component_21">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
        </data>
        <data name="component_22">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_koordinatenvektoren'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_koordinatenvektoren'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">3</data></unit><unit name="subtask_1"><data name="coordsvector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="\\vec{v}_{BStandard} = " mf:type="vector">
  <mrow mf:field="real" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="real" mf:type="number">-1</mn></mrow><mrow><mn mf:field="real" mf:type="number">2</mn></mrow></mfrac></mrow></mrow>
  <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">2</mn></mrow>
</mrow></data></unit><unit name="subtask_2"><data name="coordsvector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="\\vec{v}_{B1} = " mf:type="vector">
  <mrow mf:field="real" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="real" mf:type="number">-1</mn></mrow><mrow><mn mf:field="real" mf:type="number">6</mn></mrow></mfrac></mrow></mrow>
  <mrow mf:field="real" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="real" mf:type="number">13</mn></mrow><mrow><mn mf:field="real" mf:type="number">6</mn></mrow></mfrac></mrow></mrow>
</mrow></data></unit><unit name="subtask_3"><data name="coordsvector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="\\vec{v}_{B2} = " mf:type="vector">
  <mrow mf:field="real" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="real" mf:type="number">11</mn></mrow><mrow><mn mf:field="real" mf:type="number">24</mn></mrow></mfrac></mrow></mrow>
  <mrow mf:field="real" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="real" mf:type="number">5</mn></mrow><mrow><mn mf:field="real" mf:type="number">24</mn></mrow></mfrac></mrow></mrow>
</mrow></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_darstellende-matrix_eulerableitung'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_darstellende-matrix_eulerableitung'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="basis">
        <unit name="polynom_1">
          <data name="coefficint_0">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficint_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
          </data>
          <data name="coefficint_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">6</mn>
          </data>
        </unit>
        <unit name="polynom_2">
          <data name="coefficint_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
          </data>
          <data name="coefficint_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
        </unit>
        <unit name="polynom_3">
          <data name="coefficint_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_darstellende-matrix_eulerableitung'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_darstellende-matrix_eulerableitung'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">1</data></unit><unit name="subtask_1"><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="L_B = " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">10</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">2</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">2</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');



INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_lineare_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_komposition_von_abbildungen'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_lineare_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_komposition_von_abbildungen'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <data name="type">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
      </data>
      <data name="switch">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
      </data>
      <data name="coefficient1">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
      </data>
      <data name="coefficient2">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
      </data>
      <data name="coefficient3">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
      </data>
      <data name="coefficient4">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
      </data>
      <data name="p1">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
      </data>
      <data name="p2">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
      </data>
      <data name="q1">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
      </data>
      <data name="q2">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
      </data>
      <data name="r1">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
      </data>
      <data name="r2">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
      </data>
    </unit>
  </unit>
</data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_lineare_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_komposition_von_abbildungen'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_lineare_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_komposition_von_abbildungen'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">2</data></unit><unit name="subtask_1"><data name="validationCheck1"><mn xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:hidden="true" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">1</mn></data><data name="validationCheck2"><mn xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:hidden="true" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">1</mn></data><data name="vector"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:label="F(p) =" mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mrow><mo>(</mo><mrow><mn mf:field="rational" mf:type="number">-2</mn><mo>*</mo><msup><mi>x</mi><mn mf:field="rational" mf:type="number">2</mn></msup></mrow><mo>)</mo><mo>+</mo><mo>(</mo><mrow><mn mf:field="rational" mf:type="number">3</mn><mo>*</mo><mi>x</mi></mrow><mo>)</mo><mo>-</mo><mo>(</mo><mn mf:field="rational" mf:type="number">1</mn><mo>)</mo></mrow></mrow></data></unit><unit name="subtask_2"><data name="validationCheck1"><mn xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:hidden="true" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">1</mn></data><data name="validationCheck2"><mn xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:hidden="true" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">1</mn></data><data name="vector"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="G(q) =" mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mn mf:field="rational" mf:type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mn mf:field="rational" mf:type="number">6</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mn mf:field="rational" mf:type="number">3</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_lineare_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_lineare_abbildungen_konzepte1'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_lineare_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_lineare_abbildungen_konzepte1'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <data name="coefficient">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-14</mn>
      </data>
    </unit>
  </unit>
</data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_lineare_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_lineare_abbildungen_konzepte1'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_lineare_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_lineare_abbildungen_konzepte1'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">5</data></unit><unit name="subtask_1"><data name="matrix_A"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="A = " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-14</mn></mrow>
    </mtd>
  </mtr>
</mtable></data><data name="matrix_B"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="B = " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="matrix_A"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="A = " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-14</mn></mrow>
    </mtd>
  </mtr>
</mtable></data><data name="matrix_B"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="B = " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">2</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">3</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">4</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_3"><data name="matrix_A"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="A = " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">2</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">3</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-20</mn></mrow>
    </mtd>
  </mtr>
</mtable></data><data name="matrix_B"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="B = " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">2</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">3</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">4</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_4"><data name="matrix_A"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="A = " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">2</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">3</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-20</mn></mrow>
    </mtd>
  </mtr>
</mtable></data><data name="matrix_B"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="B = " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_5"><data name="matrix_A"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="A = " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-15</mn></mrow>
    </mtd>
  </mtr>
</mtable></data><data name="matrix_B"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="B = " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_lineare_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_inverse_abbildung'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_lineare_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_inverse_abbildung'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="coefficients">
        <data name="coefficient_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">10</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
        </data>
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">15</mn>
        </data>
        <data name="coefficient_0">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">6</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_lineare_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_inverse_abbildung'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_lineare_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_inverse_abbildung'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">1</data></unit><unit name="subtask_1"><data name="inverse_map"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="T^{-1}(kx^3+lx^2+mx+n) = " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">11</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">9</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">18</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">12</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');



INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_multiplikation_von_matrizen'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_multiplikation_von_matrizen'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">9</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-6</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-9</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-6</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-7</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-8</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
        </unit>
        <unit name="matrix_3">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-6</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-7</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-7</mn>
          </data>
        </unit>
        <unit name="matrix_4">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-10</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">8</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">6</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-5</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-4</mn>
          </data>
        </unit>
        <unit name="matrix_5">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-6</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">7</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
          </data>
        </unit>
        <unit name="matrix_6">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">6</mn>
          </data>
        </unit>
        <unit name="matrix_7">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_multiplikation_von_matrizen'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_multiplikation_von_matrizen'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">4</data><unit name="subtask_2"><data name="selectedMatrix_1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="op-number" mf:type="matrix">
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">0</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">5</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">9</mn>
          
            </mtd>
          </mtr>
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-6</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">4</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-9</mn>
          
            </mtd>
          </mtr>
        </mtable></data><data name="selectedMatrix_2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="op-number" mf:type="matrix">
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-10</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">2</mn>
          
            </mtd>
          </mtr>
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">8</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">6</mn>
          
            </mtd>
          </mtr>
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-5</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-4</mn>
          
            </mtd>
          </mtr>
        </mtable></data></unit><unit name="subtask_3"><data name="selectedMatrix_1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="op-number" mf:type="matrix">
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-3</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-6</mn>
          
            </mtd>
          </mtr>
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-7</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-7</mn>
          
            </mtd>
          </mtr>
        </mtable></data><data name="selectedMatrix_2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="op-number" mf:type="matrix">
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-6</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-1</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">3</mn>
          
            </mtd>
          </mtr>
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">7</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">5</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">5</mn>
          
            </mtd>
          </mtr>
        </mtable></data></unit><unit name="subtask_4"><data name="selectedMatrix_1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:type="matrix">
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">2</mn>
          
            </mtd>
          </mtr>
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-1</mn>
          
            </mtd>
          </mtr>
        </mtable></data><data name="selectedMatrix_2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:type="matrix">
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">2</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-1</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">6</mn>
          
            </mtd>
          </mtr>
        </mtable></data></unit></unit><unit name="subtask_1"><data name="format_A1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:label="Format von A_1:" mf:type="dimension"><mn mf:field="int" mf:type="number">2</mn>x<mn mf:field="int" mf:type="number">3</mn></mrow></data><data name="format_A2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:label="Format von A_2:" mf:type="dimension"><mn mf:field="int" mf:type="number">2</mn>x<mn mf:field="int" mf:type="number">2</mn></mrow></data><data name="format_A3"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:label="Format von A_3:" mf:type="dimension"><mn mf:field="int" mf:type="number">2</mn>x<mn mf:field="int" mf:type="number">2</mn></mrow></data><data name="format_A4"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:label="Format von A_4:" mf:type="dimension"><mn mf:field="int" mf:type="number">3</mn>x<mn mf:field="int" mf:type="number">2</mn></mrow></data><data name="format_A5"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:label="Format von A_5:" mf:type="dimension"><mn mf:field="int" mf:type="number">2</mn>x<mn mf:field="int" mf:type="number">3</mn></mrow></data><data name="format_A6"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:label="Format von A_6:" mf:type="dimension"><mn mf:field="int" mf:type="number">1</mn>x<mn mf:field="int" mf:type="number">3</mn></mrow></data><data name="format_A7"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:label="Format von A_7:" mf:type="dimension"><mn mf:field="int" mf:type="number">2</mn>x<mn mf:field="int" mf:type="number">1</mn></mrow></data></unit><unit name="subtask_2"><data name="matrix_1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="real" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">5</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">9</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">-6</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">4</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">-9</mn>
    </mtd>
  </mtr>
</mtable></data><data name="selectedMatrix_1">common/problem/matrices/matrix_1</data><data name="matrix_2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="real" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">5</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">9</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">-6</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">4</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">-9</mn>
    </mtd>
  </mtr>
</mtable></data><data name="selectedMatrix_2">common/problem/matrices/matrix_4</data><data name="matrix_sum"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="op-number" mf:label="Matrixprodukt: " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-5</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-6</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">137</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">48</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_3"><data name="matrix_1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="real" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">-3</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">-6</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">-7</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">-7</mn>
    </mtd>
  </mtr>
</mtable></data><data name="selectedMatrix_1">common/problem/matrices/matrix_3</data><data name="matrix_2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="real" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">-3</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">-6</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">-7</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">-7</mn>
    </mtd>
  </mtr>
</mtable></data><data name="selectedMatrix_2">common/problem/matrices/matrix_5</data><data name="matrix_sum"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="op-number" mf:label="Matrixprodukt: " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-24</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-26</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-39</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-7</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-28</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-56</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_4"><data name="matrix_1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">2</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">-1</mn>
    </mtd>
  </mtr>
</mtable></data><data name="selectedMatrix_1">common/problem/matrices/matrix_7</data><data name="matrix_2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">2</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">-1</mn>
    </mtd>
  </mtr>
</mtable></data><data name="selectedMatrix_2">common/problem/matrices/matrix_6</data><data name="matrix_sum"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="Matrixprodukt: " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">4</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-2</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">12</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-2</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-6</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_matrix_typen'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_matrix_typen'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-10</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-7</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-5</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-7</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">8</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">10</mn>
          </data>
        </unit>
        <unit name="matrix_3">
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">10</mn>
          </data>
        </unit>
        <unit name="matrix_5">
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">7</mn>
          </data>
        </unit>
        <unit name="matrix_4">
          <data name="coefficient_42">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">8</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_matrix_typen'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_matrix_typen'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">4</data></unit><unit name="subtask_1"><data name="matrix1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="complex" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">7</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">7</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">1</mn>
    </mtd>
  </mtr>
</mtable></data><data name="tmp1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="complex" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">7</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">1</mn>
    </mtd>
  </mtr>
</mtable></data><data name="matrix2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="complex" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" mf:field="complex" mf:type="number"><mn xmlns="http://www.w3.org/1998/Math/MathML" mf:field="real" mf:type="number">1</mn><mn xmlns="http://www.w3.org/1998/Math/MathML" mf:field="real" mf:type="number">8</mn></cnum>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" mf:field="complex" mf:type="number"><mn xmlns="http://www.w3.org/1998/Math/MathML" mf:field="real" mf:type="number">1</mn><mn xmlns="http://www.w3.org/1998/Math/MathML" mf:field="real" mf:type="number">8</mn></cnum>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
  </mtr>
</mtable></data><data name="tmp2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="complex" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" mf:field="complex" mf:type="number"><mn xmlns="http://www.w3.org/1998/Math/MathML" mf:field="real" mf:type="number">1</mn><mn xmlns="http://www.w3.org/1998/Math/MathML" mf:field="real" mf:type="number">8</mn></cnum>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
  </mtr>
</mtable></data><data name="matrix3"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="complex" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">8</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">4</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">8</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">3</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">4</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">3</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">10</mn>
    </mtd>
  </mtr>
</mtable></data><data name="tmp3"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="complex" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
    <mtd>
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">8</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">4</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">3</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">10</mn>
    </mtd>
  </mtr>
</mtable></data><data name="matrix4"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="complex" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">-10</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-7</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-2</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">-7</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-2</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-5</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">-2</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-5</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-7</mn>
    </mtd>
  </mtr>
</mtable></data><data name="tmp4"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="complex" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">-10</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-7</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-2</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-2</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-5</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
    <mtd>
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-7</mn>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="matrix1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="complex" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" mf:field="complex" mf:type="number"><mn xmlns="http://www.w3.org/1998/Math/MathML" mf:field="real" mf:type="number">1</mn><mn xmlns="http://www.w3.org/1998/Math/MathML" mf:field="real" mf:type="number">8</mn></cnum>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" mf:field="complex" mf:type="number"><mn xmlns="http://www.w3.org/1998/Math/MathML" mf:field="real" mf:type="number">-1</mn><mn xmlns="http://www.w3.org/1998/Math/MathML" mf:field="real" mf:type="number">-8</mn></cnum>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
  </mtr>
</mtable></data><data name="tmp1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="complex" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" mf:field="complex" mf:type="number"><mn xmlns="http://www.w3.org/1998/Math/MathML" mf:field="real" mf:type="number">1</mn><mn xmlns="http://www.w3.org/1998/Math/MathML" mf:field="real" mf:type="number">8</mn></cnum>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_3"><data name="matrix1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="complex" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" mf:field="complex" mf:type="number"><mn xmlns="http://www.w3.org/1998/Math/MathML" mf:field="real" mf:type="number">1</mn><mn xmlns="http://www.w3.org/1998/Math/MathML" mf:field="real" mf:type="number">8</mn></cnum>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" mf:field="complex" mf:type="number"><mn xmlns="http://www.w3.org/1998/Math/MathML" mf:field="real" mf:type="number">1</mn><mn xmlns="http://www.w3.org/1998/Math/MathML" mf:field="real" mf:type="number">-8</mn></cnum>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
  </mtr>
</mtable></data><data name="tmp1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="complex" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" mf:field="complex" mf:type="number"><mn xmlns="http://www.w3.org/1998/Math/MathML" mf:field="real" mf:type="number">1</mn><mn xmlns="http://www.w3.org/1998/Math/MathML" mf:field="real" mf:type="number">8</mn></cnum>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
  </mtr>
</mtable></data><data name="matrix2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="complex" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">7</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">7</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">1</mn>
    </mtd>
  </mtr>
</mtable></data><data name="tmp2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="complex" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">7</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">1</mn>
    </mtd>
  </mtr>
</mtable></data><data name="matrix3"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="complex" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">8</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">4</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">8</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">3</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">4</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">3</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">10</mn>
    </mtd>
  </mtr>
</mtable></data><data name="tmp3"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="complex" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
    <mtd>
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">8</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">4</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">3</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">10</mn>
    </mtd>
  </mtr>
</mtable></data><data name="matrix4"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="complex" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">-10</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-7</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-2</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">-7</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-2</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-5</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">-2</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-5</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-7</mn>
    </mtd>
  </mtr>
</mtable></data><data name="tmp4"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="complex" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">-10</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-7</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-2</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-2</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-5</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
    <mtd>
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-7</mn>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_4"><data name="matrix1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">-10</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-7</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-2</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-2</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-5</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">2</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-7</mn>
    </mtd>
  </mtr>
</mtable></data><data name="tmp1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">-10</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-7</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-2</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-2</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-5</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
    <mtd>
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-7</mn>
    </mtd>
  </mtr>
</mtable></data><data name="matrix2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">7</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">1</mn>
    </mtd>
  </mtr>
</mtable></data><data name="tmp2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">7</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">1</mn>
    </mtd>
  </mtr>
</mtable></data><data name="matrix3"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" mf:field="complex" mf:type="number"><mn mf:field="real" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">1</mn><mn mf:field="real" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">8</mn></cnum>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
  </mtr>
</mtable></data><data name="tmp3"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" mf:field="complex" mf:type="number"><mn mf:field="real" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">1</mn><mn mf:field="real" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">8</mn></cnum>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_matrizen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_kern_bild_matrix-abbildung'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_matrizen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_kern_bild_matrix-abbildung'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector_1">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
        </data>
      </unit>
      <unit name="vector_2">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
        </data>
      </unit>
      <unit name="vector_3">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_vektorraeume_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_koordinaten_basen_in_3-dim_VR'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_koordinaten_basen_in_3-dim_VR'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="basis_1">
        <data name="factor_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
        </data>
        <data name="factor_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
        <unit name="vector_1">
          <data name="coefficient_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
        </unit>
        <unit name="vector_2">
          <data name="coefficient_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
          <data name="coefficient_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="coefficient_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
        </unit>
      </unit>
      <unit name="vector_v">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
        <data name="coefficient_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
        </data>
      </unit>
      <unit name="basis_2">
        <data name="factor_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
        </data>
        <data name="factor_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
        <unit name="vector_1">
          <data name="coefficient_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficient_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
          </data>
        </unit>
        <unit name="vector_2">
          <data name="coefficient_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
          <data name="coefficient_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
          <data name="coefficient_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
        </unit>
      </unit>
      <unit name="vector_m">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
        </data>
        <data name="coefficient_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
        </data>
      </unit>
      <unit name="basis_3">
        <data name="factor_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
        </data>
        <data name="factor_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
        </data>
        <unit name="vector_1">
          <data name="coefficient_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficient_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
        </unit>
        <unit name="vector_2">
          <data name="coefficient_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficient_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
          <data name="coefficient_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
        </unit>
      </unit>
      <unit name="vector_p">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
        <data name="coefficient_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_vektorraeume_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_koordinaten_basen_in_3-dim_VR'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_koordinaten_basen_in_3-dim_VR'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">3</data></unit><unit name="subtask_1"><data name="coordVector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="\\vec{v_{B&#8321;}} = " mf:type="vector">
  <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">-12</mn></mrow><mrow><mn mf:field="rational" mf:type="number">7</mn></mrow></mfrac></mrow></mrow>
  <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">-9</mn></mrow><mrow><mn mf:field="rational" mf:type="number">7</mn></mrow></mfrac></mrow></mrow>
  <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">-2</mn></mrow><mrow><mn mf:field="rational" mf:type="number">7</mn></mrow></mfrac></mrow></mrow>
</mrow></data></unit><unit name="subtask_2"><data name="coordVector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="\\vec{M_{B&#8322;}} = " mf:type="vector">
  <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">10</mn></mrow><mrow><mn mf:field="rational" mf:type="number">9</mn></mrow></mfrac></mrow></mrow>
  <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">-2</mn></mrow><mrow><mn mf:field="rational" mf:type="number">15</mn></mrow></mfrac></mrow></mrow>
  <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">-7</mn></mrow><mrow><mn mf:field="rational" mf:type="number">45</mn></mrow></mfrac></mrow></mrow>
</mrow></data></unit><unit name="subtask_3"><data name="coordVector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="\\vec{p_{B&#8323;}} = " mf:type="vector">
  <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">6</mn></mrow><mrow><mn mf:field="rational" mf:type="number">53</mn></mrow></mfrac></mrow></mrow>
  <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">-7</mn></mrow><mrow><mn mf:field="rational" mf:type="number">53</mn></mrow></mfrac></mrow></mrow>
  <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">-11</mn></mrow><mrow><mn mf:field="rational" mf:type="number">53</mn></mrow></mfrac></mrow></mrow>
</mrow></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_vektorraeume_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_erzeugendensystem'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_erzeugendensystem'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="polynom_1">
        <data name="coefficient_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
        </data>
        <data name="coefficient_0">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
        </data>
      </unit>
      <unit name="polynom_4">
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-4</mn>
        </data>
      </unit>
      <unit name="polynom_7">
        <data name="coefficient_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-5</mn>
        </data>
        <data name="coefficient_0">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-5</mn>
        </data>
      </unit>
      <unit name="polynom_2">
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
        </data>
      </unit>
      <unit name="polynom_8">
        <data name="coefficient_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
        </data>
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
        </data>
      </unit>
      <unit name="polynom_3">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
        </data>
        <data name="coefficient_0">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
      </unit>
      <unit name="polynom_6">
        <data name="coefficient_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-4</mn>
        </data>
      </unit>
      <unit name="polynom_9">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
        </data>
        <data name="coefficient_0">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_vektorraeume_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_erzeugendensystem'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_erzeugendensystem'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">4</data><unit name="subtask_1"><data name="selected_1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="op-number">
                    
          <mn mf:field="int" mf:type="number">-4</mn>
        
                    <msup>
                      <mi>x</mi>
                      <mn>2</mn>
                    </msup>
                  </mrow></data><data name="selected_2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="op-number">
                      
          <mn mf:field="int" mf:type="number">3</mn>
        
                      <mi>x</mi>
                      <mrow>
                        <mo>(</mo>
                        <mi>x</mi>
                        <mo>+</mo>
                        <mn>1</mn>
                        <mo>)</mo>
                      </mrow>
                    </mrow></data><data name="selected_3"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="op-number">
                        
          <mn mf:field="int" mf:type="number">5</mn>
        
                        <msup>
                          <mi>x</mi>
                          <mn>3</mn>
                        </msup>
                        <mo>+</mo>
          <mn mf:field="int" mf:type="number">4</mn>
        
                        <msup>
                          <mi>x</mi>
                          <mn>2</mn>
                        </msup>
                        <mo>+</mo>
          <mn mf:field="int" mf:type="number">4</mn>
        
                        <mi>x</mi>
                      </mrow></data></unit><unit name="subtask_2"><data name="selected_1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="op-number">
                    
          <mn mf:field="int" mf:type="number">-4</mn>
        
                    <msup>
                      <mi>x</mi>
                      <mn>2</mn>
                    </msup>
                  </mrow></data><data name="selected_2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="op-number">
                      
          <mn mf:field="int" mf:type="number">3</mn>
        
                      <mi>x</mi>
                      <mrow>
                        <mo>(</mo>
                        <mi>x</mi>
                        <mo>+</mo>
                        <mn>1</mn>
                        <mo>)</mo>
                      </mrow>
                    </mrow></data><data name="selected_3"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="op-number">
                        <mi>x</mi>
                        <mo>+</mo>
                        <mn>1</mn>
                      </mrow></data><data name="selected_4"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="op-number">
                          
          <mn mf:field="int" mf:type="number">5</mn>
        
                          <mi>x</mi>
                          <mo>+</mo>
          <mn mf:field="int" mf:type="number">1</mn>
        
                        </mrow></data><data name="selected_5"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="op-number">
                            
          <mn mf:field="int" mf:type="number">-2</mn>
        
                            <mi>x</mi>
                            <mo>+</mo>
          <mn mf:field="int" mf:type="number">1</mn>
        
                          </mrow></data></unit><unit name="subtask_3"><data name="selected_1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="op-number">
                  
          <mn mf:field="int" mf:type="number">-2</mn>
        
                  <msup>
                    <mi>x</mi>
                    <mn>3</mn>
                  </msup>
                  <mo>+</mo>
          <mn mf:field="int" mf:type="number">5</mn>
        
                </mrow></data><data name="selected_2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="op-number">
                    
          <mn mf:field="int" mf:type="number">-4</mn>
        
                    <msup>
                      <mi>x</mi>
                      <mn>2</mn>
                    </msup>
                  </mrow></data><data name="selected_3"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="op-number">
                    
          <mn mf:field="int" mf:type="number">-5</mn>
        
                    <msup>
                      <mi>x</mi>
                      <mn>3</mn>
                    </msup>
                    <mo>+</mo>
          <mn mf:field="int" mf:type="number">-5</mn>
        
                  </mrow></data></unit><unit name="subtask_4"><data name="selected_1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="op-number">
                  
          <mn mf:field="int" mf:type="number">-2</mn>
        
                  <msup>
                    <mi>x</mi>
                    <mn>3</mn>
                  </msup>
                  <mo>+</mo>
          <mn mf:field="int" mf:type="number">5</mn>
        
                </mrow></data><data name="selected_2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="op-number">
                    
          <mn mf:field="int" mf:type="number">-5</mn>
        
                    <msup>
                      <mi>x</mi>
                      <mn>3</mn>
                    </msup>
                    <mo>+</mo>
          <mn mf:field="int" mf:type="number">-5</mn>
        
                  </mrow></data><data name="selected_3"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="op-number">
                        <mi>x</mi>
                        <mo>+</mo>
                        <mn>1</mn>
                      </mrow></data><data name="selected_4"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="op-number">
                          
          <mn mf:field="int" mf:type="number">5</mn>
        
                          <mi>x</mi>
                          <mo>+</mo>
          <mn mf:field="int" mf:type="number">1</mn>
        
                        </mrow></data><data name="selected_5"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="op-number">
                            
          <mn mf:field="int" mf:type="number">-2</mn>
        
                            <mi>x</mi>
                            <mo>+</mo>
          <mn mf:field="int" mf:type="number">1</mn>
        
                          </mrow></data></unit></unit><unit name="subtask_1"><data name="selected_1">common/problem/polynom_4</data><data name="selected_2">common/problem/polynom_2</data><data name="selected_3">common/problem/polynom_8</data><data name="nrOfElements"><mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:hidden="true" mf:type="number">3</mn></data></unit><unit name="subtask_2"><data name="selected_1">common/problem/polynom_4</data><data name="selected_2">common/problem/polynom_2</data><data name="selected_3">common/problem/polynom_5</data><data name="selected_4">common/problem/polynom_3</data><data name="selected_5">common/problem/polynom_9</data><data name="nrOfElements"><mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:hidden="true" mf:type="number">5</mn></data></unit><unit name="subtask_3"><data name="selected_1">common/problem/polynom_1</data><data name="selected_2">common/problem/polynom_4</data><data name="selected_3">common/problem/polynom_7</data><data name="nrOfElements"><mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:hidden="true" mf:type="number">3</mn></data></unit><unit name="subtask_4"><data name="selected_1">common/problem/polynom_1</data><data name="selected_2">common/problem/polynom_7</data><data name="selected_3">common/problem/polynom_5</data><data name="selected_4">common/problem/polynom_3</data><data name="selected_5">common/problem/polynom_9</data><data name="nrOfElements"><mn xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:hidden="true" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">5</mn></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_vektorraeume_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_basis_von_matrizen'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_basis_von_matrizen'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-5</mn>
          </data>
        </unit>
        <unit name="matrix_3">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
          </data>
        </unit>
        <unit name="matrix_4">
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
        </unit>
        <unit name="matrix_6">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
          </data>
        </unit>
        <unit name="matrix_7">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
        </unit>
        <unit name="matrix_8">
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-5</mn>
          </data>
        </unit>
        <unit name="matrix_9">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-5</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_vektorraeume_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_basis_von_matrizen'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_basis_von_matrizen'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">2</data><unit name="subtask_1"><data name="selected_1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="real" mf:type="matrix">
            <mtr>
              <mtd align="center">
                <mn>0</mn>
              </mtd>
              <mtd align="center">
                
            <mn mf:field="int" mf:type="number">5</mn>
          
              </mtd>
            </mtr>
            <mtr>
              <mtd align="center">
                
            <mn mf:field="int" mf:type="number">-2</mn>
          
              </mtd>
              <mtd align="center">
                
            <mn mf:field="int" mf:type="number">-5</mn>
          
              </mtd>
            </mtr>
          </mtable></data><data name="selected_2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="real" mf:type="matrix">
            <mtr>
              <mtd align="center">
                
            <mn mf:field="int" mf:type="number">2</mn>
          
              </mtd>
              <mtd align="center">
                <mn>0</mn>
              </mtd>
            </mtr>
            <mtr>
              <mtd align="center">
                <mn>0</mn>
              </mtd>
              <mtd align="center">
                
            <mn mf:field="int" mf:type="number">-1</mn>
          
              </mtd>
            </mtr>
          </mtable></data><data name="selected_3"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="real" mf:type="matrix">
            <mtr>
              <mtd align="center">
                
            <mn mf:field="int" mf:type="number">-2</mn>
          
              </mtd>
              <mtd align="center">
                
            <mn mf:field="int" mf:type="number">4</mn>
          
              </mtd>
            </mtr>
            <mtr>
              <mtd align="center">
                
            <mn mf:field="int" mf:type="number">1</mn>
          
              </mtd>
              <mtd align="center">
                
            <mn mf:field="int" mf:type="number">5</mn>
          
              </mtd>
            </mtr>
          </mtable></data><data name="selected_4"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="real" mf:type="matrix">
            <mtr>
              <mtd align="center">
                <mn>0</mn>
              </mtd>
              <mtd align="center">
                <mn>0</mn>
              </mtd>
            </mtr>
            <mtr>
              <mtd align="center">
                
            <mn mf:field="int" mf:type="number">3</mn>
          
              </mtd>
              <mtd align="center">
                <mn>0</mn>
              </mtd>
            </mtr>
          </mtable></data></unit><unit name="subtask_2"><data name="selected_1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:type="matrix">
            <mtr>
              <mtd align="center">
                
            <mn mf:field="int" mf:type="number">3</mn>
          
              </mtd>
              <mtd align="center">
                
            <mn mf:field="int" mf:type="number">5</mn>
          
              </mtd>
            </mtr>
            <mtr>
              <mtd align="center">
                <mn>0</mn>
              </mtd>
              <mtd align="center">
                <mn>0</mn>
              </mtd>
            </mtr>
          </mtable></data><data name="selected_2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:type="matrix">
            <mtr>
              <mtd align="center">
                
            <mn mf:field="int" mf:type="number">1</mn>
          
              </mtd>
              <mtd align="center">
                <mn>0</mn>
              </mtd>
            </mtr>
            <mtr>
              <mtd align="center">
                <mn>0</mn>
              </mtd>
              <mtd align="center">
                
            <mn mf:field="int" mf:type="number">1</mn>
          
              </mtd>
            </mtr>
          </mtable></data><data name="selected_3"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:type="matrix">
            <mtr>
              <mtd align="center">
                <mn>0</mn>
              </mtd>
              <mtd align="center">
                
            <mn mf:field="int" mf:type="number">-5</mn>
          
              </mtd>
            </mtr>
            <mtr>
              <mtd align="center">
                
            <mn mf:field="int" mf:type="number">-5</mn>
          
              </mtd>
              <mtd align="center">
                <mn>0</mn>
              </mtd>
            </mtr>
          </mtable></data><data name="selected_4"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:type="matrix">
            <mtr>
              <mtd align="center">
                
            <mn mf:field="int" mf:type="number">-5</mn>
          
              </mtd>
              <mtd align="center">
                
            <mn mf:field="int" mf:type="number">-3</mn>
          
              </mtd>
            </mtr>
            <mtr>
              <mtd align="center">
                <mn>0</mn>
              </mtd>
              <mtd align="center">
                <mn>0</mn>
              </mtd>
            </mtr>
          </mtable></data></unit></unit><unit name="subtask_1"><data name="selected_1">common/problem/matrices/matrix_8</data><data name="selected_2">common/problem/matrices/matrix_7</data><data name="selected_3">common/problem/matrices/matrix_6</data><data name="selected_4">common/problem/matrices/matrix_4</data><data name="nrOfElements"><mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:hidden="true" mf:type="number">4</mn></data></unit><unit name="subtask_2"><data name="selected_1">common/problem/matrices/matrix_3</data><data name="selected_2">common/problem/matrices/matrix_1</data><data name="selected_3">common/problem/matrices/matrix_2</data><data name="selected_4">common/problem/matrices/matrix_9</data><data name="nrOfElements"><mn xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:hidden="true" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">4</mn></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_determinante_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_spiegelung'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_2_orthogonal_and_symmetric_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_determinante_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_spiegelung'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_2_orthogonal_and_symmetric_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_determinante_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_spiegelung'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_2_orthogonal_and_symmetric_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_determinante_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_spiegelung'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_2_orthogonal_and_symmetric_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">1</data></unit><unit name="subtask_1"><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="A = " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0,6</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-0,8</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-0,8</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-0,6</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_determinante_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_determinante'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_3_multilinear_forms_and_tensors'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_determinante_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_determinante'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_3_multilinear_forms_and_tensors')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrix_1">
        <data name="component_11">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
        <data name="component_12">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
        </data>
        <data name="component_13">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
        </data>
        <data name="component_14">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
        </data>
        <data name="component_22">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-4</mn>
        </data>
        <data name="component_23">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
        </data>
        <data name="component_24">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
        </data>
        <data name="component_33">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
        <data name="component_34">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
        </data>
        <data name="component_44">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
        </data>
      </unit>
      <unit name="matrix_2">
        <data name="component_11">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
        </data>
        <data name="component_12">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
        </data>
        <data name="component_13">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
        </data>
        <data name="component_14">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-4</mn>
        </data>
        <data name="component_21">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
        <data name="component_23">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-5</mn>
        </data>
        <data name="component_31">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
        </data>
        <data name="component_32">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
        </data>
        <data name="component_33">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
        </data>
        <data name="component_34">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
        </data>
        <data name="component_41">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
        </data>
        <data name="component_42">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
        </data>
        <data name="component_43">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
        </data>
        <data name="component_44">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
        </data>
      </unit>
      <unit name="matrix_3">
        <data name="component_12">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">7</mn>
        </data>
        <data name="component_13">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
        </data>
        <data name="component_23">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">7</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_determinante_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_determinante'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_3_multilinear_forms_and_tensors'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_determinante_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_determinante'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_3_multilinear_forms_and_tensors')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">3</data></unit><unit name="subtask_1"><data name="det"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="det(M_1) = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">4</mn></mrow></data></unit><unit name="subtask_2"><data name="det"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="det(M_2) = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">16</mn></mrow></data></unit><unit name="subtask_3"><data name="det"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="det(M_3) = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">0</mn></mrow></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_determinante_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_qr-zerlegung'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_2_orthogonal_and_symmetric_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_determinante_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_qr-zerlegung'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_2_orthogonal_and_symmetric_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">7</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">7</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-9</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">8</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-6</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_determinante_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_qr-zerlegung'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_2_orthogonal_and_symmetric_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_determinante_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_qr-zerlegung'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_2_orthogonal_and_symmetric_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">2</data></unit><unit name="subtask_1"><data name="matrix_q"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="Q = " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">7</mn></mrow><mrow><msqrt><mn mf:field="rational" mf:type="number">98</mn></msqrt></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">7</mn></mrow><mrow><msqrt><mn mf:field="rational" mf:type="number">98</mn></msqrt></mrow></mfrac></mrow></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">-7</mn></mrow><mrow><msqrt><mn mf:field="rational" mf:type="number">98</mn></msqrt></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">7</mn></mrow><mrow><msqrt><mn mf:field="rational" mf:type="number">98</mn></msqrt></mrow></mfrac></mrow></mrow>
    </mtd>
  </mtr>
</mtable></data><data name="matrix_r"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="R = " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mn mf:field="rational" mf:type="number">98</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mn mf:field="rational" mf:type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mn mf:field="rational" mf:type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mn mf:field="rational" mf:type="number">98</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="matrix_q"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="Q = " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">2</mn></mrow><mrow><mn mf:field="rational" mf:type="number">3</mn></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">-2</mn></mrow><mrow><mn mf:field="rational" mf:type="number">3</mn></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mrow><mn mf:field="rational" mf:type="number">-37</mn><mo>*</mo><msqrt><mn mf:field="rational" mf:type="number">1039</mn></msqrt><mo>*</mo><mfrac><mn mf:field="int" mf:type="number">1</mn><mn mf:field="rational" mf:type="number">2078</mn></mfrac></mrow></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">2</mn></mrow><mrow><mn mf:field="rational" mf:type="number">3</mn></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">1</mn></mrow><mrow><mn mf:field="rational" mf:type="number">3</mn></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mrow><mn mf:field="rational" mf:type="number">35</mn><mo>*</mo><msqrt><mn mf:field="rational" mf:type="number">1039</mn></msqrt><mo>*</mo><mfrac><mn mf:field="int" mf:type="number">1</mn><mn mf:field="rational" mf:type="number">1039</mn></mfrac></mrow></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">1</mn></mrow><mrow><mn mf:field="rational" mf:type="number">3</mn></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">2</mn></mrow><mrow><mn mf:field="rational" mf:type="number">3</mn></mrow></mfrac></mrow></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mrow><mn mf:field="rational" mf:type="number">-37</mn><mo>*</mo><msqrt><mn mf:field="rational" mf:type="number">1039</mn></msqrt><mo>*</mo><mfrac><mn mf:field="int" mf:type="number">1</mn><mn mf:field="rational" mf:type="number">1039</mn></mfrac></mrow></mrow>
    </mtd>
  </mtr>
</mtable></data><data name="matrix_r"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="R = " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mn mf:field="rational" mf:type="number">9</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mn mf:field="rational" mf:type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mn mf:field="rational" mf:type="number">-8</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mn mf:field="rational" mf:type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mn mf:field="rational" mf:type="number">9</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mn mf:field="rational" mf:type="number">14</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mn mf:field="rational" mf:type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mn mf:field="rational" mf:type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="rational" mf:type="op-number"><mn mf:field="rational" mf:type="number">0</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_determinante_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_determinante2'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_3_multilinear_forms_and_tensors'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_determinante_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_determinante2'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_3_multilinear_forms_and_tensors')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrix_1">
        <data name="component_12">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-5</mn>
        </data>
        <data name="component_22">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-4</mn>
        </data>
      </unit>
      <unit name="matrix_2">
        <data name="component_11">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
        </data>
        <data name="component_12">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
        </data>
        <data name="component_21">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
        <data name="component_22">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-5</mn>
        </data>
      </unit>
      <unit name="matrix_3">
        <data name="component_11">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
        </data>
        <data name="component_22">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_determinante_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_determinante2'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_3_multilinear_forms_and_tensors'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_determinante_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_determinante2'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_3_multilinear_forms_and_tensors')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">2</data></unit><unit name="subtask_1"><data name="det1"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="det(A_1) = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">0</mn></mrow></data><data name="det2"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="det(A_2) = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">8</mn></mrow></data><data name="det3"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="det(A_3) = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">4</mn></mrow></data></unit><unit name="subtask_2"><data name="usermatrix1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">-2</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">2</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">-5</mn>
    </mtd>
  </mtr>
</mtable></data><data name="usermatrix2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">4</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">0</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">1</mn>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_determinante_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_determinante1'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_3_multilinear_forms_and_tensors'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_determinante_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_determinante1'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_3_multilinear_forms_and_tensors')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_determinante_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_determinante1'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_3_multilinear_forms_and_tensors'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_determinante_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_determinante1'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_3_multilinear_forms_and_tensors')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">1</data></unit><unit name="subtask_1"><data name="matrix1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="F&#252;r det(A)=0 : A = " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-2</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">2</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
  </mtr>
</mtable></data><data name="matrix2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="F&#252;r det(A)&#8800;0 : A = " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-2</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">2</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">2</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_diagonalisierbare_matrizen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_diagonalisierbare_matrizen'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_diagonalisierbare_matrizen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_diagonalisierbare_matrizen'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <data name="lambda1">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
      </data>
      <data name="lambda2">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
      </data>
    </unit>
  </unit>
</data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_diagonalisierbare_matrizen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_diagonalisierbare_matrizen'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_diagonalisierbare_matrizen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_diagonalisierbare_matrizen'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">4</data></unit><unit name="subtask_1"><data name="l1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="&#955;_1 = " mf:type="op-number"><mn mf:field="real" mf:type="number">2</mn></mrow></data><data name="l2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="&#955;_2 = " mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow></data></unit><unit name="subtask_2"><data name="matrix_invers"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="op-number" mf:label="S^{-1} = " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">3</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-4</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-2</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">3</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_3"><data name="matrix_product"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="S^{-1}AS = " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">2</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_4"><data name="selected_0"><mtext xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="string" mf:label="symmetrisch: " mf:type="text" xmlns="http://www.w3.org/1998/Math/MathML">richtig</mtext></data><data name="selected_1"><mtext xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="string" mf:label="antisymmetrisch: " mf:type="text" xmlns="http://www.w3.org/1998/Math/MathML">falsch</mtext></data><data name="selected_2"><mtext xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="string" mf:label="eine Diagonalmatrix: " mf:type="text" xmlns="http://www.w3.org/1998/Math/MathML">richtig</mtext></data><data name="selected_3"><mtext xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="string" mf:label="eine obere Dreiecksmatrix: " mf:type="text" xmlns="http://www.w3.org/1998/Math/MathML">richtig</mtext></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_diagonalisierbare_matrizen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_diagonalisierbare-matrix2'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_diagonalisierbare_matrizen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_diagonalisierbare-matrix2'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrix">
        <data name="coefficient_11">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_diagonalisierbare_matrizen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_diagonalisierbare-matrix2'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_diagonalisierbare_matrizen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_diagonalisierbare-matrix2'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">1</data></unit><unit name="subtask_2"><data name="a"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="a = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">1</mn></mrow></data></unit><unit name="subtask_1"><data name="a"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="a = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">0</mn></mrow></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_diffgleichungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_dgl_bahnen'
       AND contained_in=section_id_for_path(0, 'content/tub/differential_equations/problems/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_diffgleichungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_dgl_bahnen'
    AND contained_in=section_id_for_path(0, 'content/tub/differential_equations/problems/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector_1">
        <data name="x">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
        </data>
        <data name="y">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
        </data>
      </unit>
      <data name="radius">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number">1</mn>
      </data>
      <data name="time">
        <mfrac xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number"><mn mf:field="int" mf:type="number">1</mn><mn mf:field="int" mf:type="number">6</mn></mfrac>
      </data>
      <unit name="vector_2">
        <data name="x">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
        <data name="y">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_schrift_kapitel_11_ss_a'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_schrift_kapitel_11_ss_a'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
</data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_lineare-abbildung-mit-eigenvektoren'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_lineare-abbildung-mit-eigenvektoren'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <data name="sum_1">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
      </data>
      <data name="sum_2">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
      </data>
    </unit>
  </unit>
</data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_lineare-abbildung-mit-eigenvektoren'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_lineare-abbildung-mit-eigenvektoren'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">2</data></unit><unit name="subtask_1"><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="M = " mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">2</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">-1</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">-1</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">2</mn>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_3"><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="M = " mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">1</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">1</mn>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="M = " mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">1,5</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">0</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">1,5</mn>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_charakteristisches-polynom'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_charakteristisches-polynom'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="coefficient_14">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficient_24">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
          <data name="coefficient_34">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
          <data name="coefficient_44">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
          <data name="coefficient_14">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="coefficient_24">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="coefficient_34">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
          <data name="coefficient_41">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
          <data name="coefficient_42">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_44">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_charakteristisches-polynom'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_charakteristisches-polynom'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">1</data></unit><unit name="subtask_2"><data name="poly2"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="p_M(x) = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mrow><msup><mi>x</mi><mn mf:field="real" mf:type="number">4</mn></msup><mo>-</mo><mo>(</mo><mrow><mn mf:field="real" mf:type="number">8</mn><mo>*</mo><msup><mi>x</mi><mn mf:field="real" mf:type="number">3</mn></msup></mrow><mo>)</mo><mo>+</mo><mo>(</mo><mrow><mn mf:field="real" mf:type="number">21</mn><mo>*</mo><msup><mi>x</mi><mn mf:field="real" mf:type="number">2</mn></msup></mrow><mo>)</mo><mo>-</mo><mo>(</mo><mrow><mn mf:field="real" mf:type="number">20</mn><mo>*</mo><mi>x</mi></mrow><mo>)</mo><mo>+</mo><mn mf:field="real" mf:type="number">6</mn></mrow></mrow></data></unit><unit name="subtask_1"><data name="poly1"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="p_M(x) = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mrow><msup><mi>x</mi><mn mf:field="real" mf:type="number">4</mn></msup><mo>+</mo><mo>(</mo><mrow><mn mf:field="real" mf:type="number">2</mn><mo>*</mo><msup><mi>x</mi><mn mf:field="real" mf:type="number">3</mn></msup></mrow><mo>)</mo><mo>-</mo><mo>(</mo><msup><mi>x</mi><mn mf:field="real" mf:type="number">2</mn></msup><mo>)</mo><mo>-</mo><mo>(</mo><mrow><mn mf:field="real" mf:type="number">2</mn><mo>*</mo><mi>x</mi></mrow><mo>)</mo></mrow></mrow></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_eigenwerte-eigenvektoren'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_eigenwerte-eigenvektoren'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <data name="omega">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
      </data>
      <data name="f">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
      </data>
    </unit>
  </unit>
</data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_eigenwerte-eigenvektoren'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_eigenvektoren-eigenwerte_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_eigenwerte-eigenvektoren'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">2</data></unit><unit name="subtask_1"><data name="p(z)"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="p(z) = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mrow><msup><mi>z</mi><mn mf:field="real" mf:type="number">2</mn></msup><mo>+</mo><mi>z</mi><mo>+</mo><mn mf:field="real" mf:type="number">9</mn></mrow></mrow></data></unit><unit name="subtask_2"><data name="f"><mn xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="f = " mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">0</mn></data><data name="z1"><cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:label="&#955;_1 = " mf:type="number"><mn mf:field="real" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">0</mn><mn mf:field="real" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">-3</mn></cnum></data><data name="z2"><cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:label="&#955;_2 = " mf:type="number"><mn mf:field="real" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">0</mn><mn mf:field="real" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">3</mn></cnum></data><data name="v1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:label="\\vec{v_1} = " mf:type="vector">
  <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" mf:field="complex" mf:type="number"><mn mf:field="real" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">0</mn><mn mf:field="real" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">-1</mn></cnum>
  <mn mf:field="complex" mf:type="number">1</mn>
</mrow></data><data name="v2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:label="\\vec{v_2} = " mf:type="vector">
  <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" mf:field="complex" mf:type="number"><mn mf:field="real" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">0</mn><mn mf:field="real" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">-1</mn></cnum>
  <mn mf:field="complex" mf:type="number">1</mn>
</mrow></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_eigenvektoren-eigenwerte_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_eigenwerte-eigenvektoren'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_eigenvektoren-eigenwerte_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_eigenwerte-eigenvektoren'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrix">
        <data name="component_11">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
        </data>
        <data name="component_12">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
        </data>
        <data name="component_22">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_eigenvektoren-eigenwerte_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_eigenwerte-eigenvektoren'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_eigenvektoren-eigenwerte_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_eigenwerte-eigenvektoren'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">1</data></unit><unit name="subtask_1"><data name="x"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="x = " mf:type="vector">
  <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-6</mn></mrow>
  <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
</mrow></data><data name="lambda"><mn xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="&#955; = " mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">-3</mn></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_eigenvektoren-eigenwerte_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_charakteristisches-polynom'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_eigenvektoren-eigenwerte_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_charakteristisches-polynom'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_eigenvektoren-eigenwerte_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_charakteristisches-polynom'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_eigenvektoren-eigenwerte_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_charakteristisches-polynom'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">1</data></unit><unit name="subtask_1"><data name="poly1"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="p_M(x) = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mrow><msup><mi>x</mi><mn mf:field="real" mf:type="number">2</mn></msup><mo>+</mo><mn mf:field="real" mf:type="number">1</mn></mrow></mrow></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_euklidische_unitaere_vektorraeume_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_standardskalarprodukt'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_1_euclidean_vector_spaces_and_the_Rn_with_scalar_product/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_euklidische_unitaere_vektorraeume_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_standardskalarprodukt'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_1_euclidean_vector_spaces_and_the_Rn_with_scalar_product/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector_a">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number">-4</mn>
        </data>
        <data name="component_2">
          <mfrac xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number"><mn mf:field="int" mf:type="number">-1</mn><mn mf:field="int" mf:type="number">2</mn></mfrac>
        </data>
      </unit>
      <unit name="vector_b">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number">1</mn>
        </data>
        <data name="component_2">
          <mfrac xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number"><mn mf:field="int" mf:type="number">-5</mn><mn mf:field="int" mf:type="number">2</mn></mfrac>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_euklidische_unitaere_vektorraeume_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_standardskalarprodukt'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_1_euclidean_vector_spaces_and_the_Rn_with_scalar_product/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_euklidische_unitaere_vektorraeume_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_standardskalarprodukt'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_1_euclidean_vector_spaces_and_the_Rn_with_scalar_product/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">1</data></unit><unit name="subtask_1"><data name="dotproduct"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="&lt;\\vec{a}, \\vec{b}&gt; = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">-2,75</mn></mrow></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_euklidische_unitaere_vektorraeume_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_senkrechte_vektoren'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_1_euclidean_vector_spaces_and_the_Rn_with_scalar_product/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_euklidische_unitaere_vektorraeume_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_senkrechte_vektoren'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_1_euclidean_vector_spaces_and_the_Rn_with_scalar_product/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector_a">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number">-2</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number">-1</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_euklidische_unitaere_vektorraeume_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_senkrechte_vektoren'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_1_euclidean_vector_spaces_and_the_Rn_with_scalar_product/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_euklidische_unitaere_vektorraeume_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_senkrechte_vektoren'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_1_euclidean_vector_spaces_and_the_Rn_with_scalar_product/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">1</data></unit><unit name="subtask_1"><data name="x"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:label="\\vec{x} = " mf:type="vector">
  <mn mf:field="rational" mf:type="number">-1</mn>
  <mn mf:field="rational" mf:type="number">2</mn>
</mrow></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_gauss_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_koeffizientenmatrix'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_gauss_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_koeffizientenmatrix'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_2">
          <data name="coefficent_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
          </data>
          <data name="coefficent_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">9</mn>
          </data>
          <data name="coefficent_14">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">7</mn>
          </data>
          <data name="b_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">8</mn>
          </data>
          <data name="coefficent_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">6</mn>
          </data>
          <data name="coefficent_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficent_24">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">20</mn>
          </data>
          <data name="b_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_gauss_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_koeffizientenmatrix'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_gauss_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_koeffizientenmatrix'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">2</data></unit><unit name="subtask_1"><data name="matrix_a"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:label="Koeffizientenmatrix: " mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="rational" mf:type="number">5</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">9</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">7</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="rational" mf:type="number">6</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">3</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">20</mn>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="matrix_b"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:label="erw. Koeffizientenmatrix: " mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="rational" mf:type="number">5</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">9</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">7</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">8</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="rational" mf:type="number">6</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">20</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">-3</mn>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_gauss_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_loesungsmenge_lgs'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_gauss_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_loesungsmenge_lgs'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
</data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_gauss_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_loesungsmenge_lgs'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_gauss_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_loesungsmenge_lgs'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">2</data></unit><unit name="subtask_1"><data name="a_m1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="m_1 = " mf:type="op-number"><mn mf:field="real" mf:type="number">4</mn></mrow></data><data name="a_b1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="n_1 = " mf:type="op-number"><mn mf:field="real" mf:type="number">2</mn></mrow></data><data name="a_m2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="m_2 = " mf:type="op-number"><mn mf:field="real" mf:type="number">-4</mn></mrow></data><data name="a_b2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="n_2 = " mf:type="op-number"><mn mf:field="real" mf:type="number">2</mn></mrow></data><data name="a_matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="op-number" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-4</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">4</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
  </mtr>
</mtable></data><data name="a_vectorb"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="op-number" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">2</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">2</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_3"><data name="c_m1"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="m_1 = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">2</mn></mrow></data><data name="c_b1"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="n_1 = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">1</mn></mrow></data><data name="c_m2"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="m_2 = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">2</mn></mrow></data><data name="c_b2"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="n_2 = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">3</mn></mrow></data><data name="c_matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-2</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-2</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
  </mtr>
</mtable></data><data name="c_vectorb"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">3</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="b_m1"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="m_1 = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">2</mn></mrow></data><data name="b_b1"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="n_1 = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">2</mn></mrow></data><data name="b_m2"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="m_2 = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">2</mn></mrow></data><data name="b_b2"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="n_2 = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">2</mn></mrow></data><data name="b_matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-2</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-2</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
  </mtr>
</mtable></data><data name="b_vectorb"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">2</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">2</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_koordinatenvektoren_polynomen'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_koordinatenvektoren_polynomen'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="polynom">
        <data name="coefficient_2">
          <mfrac xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number"><mn mf:field="int" mf:type="number">5</mn><mn mf:field="int" mf:type="number">2</mn></mfrac>
        </data>
        <data name="coefficient_1">
          <mfrac xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number"><mn mf:field="int" mf:type="number">1</mn><mn mf:field="int" mf:type="number">3</mn></mfrac>
        </data>
        <data name="coefficient_0">
          <mfrac xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number"><mn mf:field="int" mf:type="number">3</mn><mn mf:field="int" mf:type="number">5</mn></mfrac>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_koordinatenvektoren_polynomen'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_koordinatenvektoren_polynomen'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">2</data></unit><unit name="subtask_1"><data name="vector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="Koordinatenvektor \\vec{p_{B1}} = " mf:type="vector">
  <mrow mf:field="real" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="real" mf:type="number">3</mn></mrow><mrow><mn mf:field="real" mf:type="number">5</mn></mrow></mfrac></mrow></mrow>
  <mrow mf:field="real" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="real" mf:type="number">1</mn></mrow><mrow><mn mf:field="real" mf:type="number">3</mn></mrow></mfrac></mrow></mrow>
  <mrow mf:field="real" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="real" mf:type="number">5</mn></mrow><mrow><mn mf:field="real" mf:type="number">2</mn></mrow></mfrac></mrow></mrow>
</mrow></data></unit><unit name="subtask_2"><data name="vector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="Koordinatenvektor \\vec{p_{B2}} = " mf:type="vector">
  <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
  <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
  <mrow mf:field="real" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="real" mf:type="number">3</mn></mrow><mrow><mn mf:field="real" mf:type="number">5</mn></mrow></mfrac></mrow></mrow>
</mrow></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_koordinatenvektoren_in_r2'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_koordinatenvektoren_in_r2'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number">3</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number">1</mn>
        </data>
      </unit>
      <unit name="basis_1">
        <data name="component_11">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
        </data>
        <data name="component_12">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
        <data name="component_21">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
        </data>
        <data name="component_22">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_koordinatenvektoren_in_r2'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_koordinaten-abbildung_darstellende-matrizen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_koordinatenvektoren_in_r2'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">2</data></unit><unit name="subtask_1"><data name="coordsvector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="column-vector" mf:field="op-number" mf:label="\\vec{v}_{BStandard} = " mf:type="vector">
  <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">3</mn></mrow>
  <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
</mrow></data></unit><unit name="subtask_2"><data name="coordsvector"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="\\vec{v}_{B1} = " mf:type="vector">
  <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
  <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">0</mn></mrow>
</mrow></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_lineare_abbildungen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_linearitaet_der_zeilenextraktion'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_lineare_abbildungen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_linearitaet_der_zeilenextraktion'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="common">
    <unit name="problem">
      <data name="coefficient">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
      </data>
      <data name="lambda">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">14</mn>
      </data>
    </unit>
  </unit>
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-5</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_41">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
          <data name="coefficient_42">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_43">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-5</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
          </data>
          <data name="coefficient_41">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
          <data name="coefficient_42">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
          </data>
          <data name="coefficient_43">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_matrizen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_multiplikation_von_matrizen'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_matrizen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_multiplikation_von_matrizen'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-5</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-7</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-8</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-10</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">9</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-4</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_matrizen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_linearkombination_von_matrizen'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_matrizen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_linearkombination_von_matrizen'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">9</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
        <data name="coefficient_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">9</mn>
        </data>
        <data name="coefficient_4">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">8</mn>
        </data>
        <data name="coefficient_5">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
        </data>
        <data name="coefficient_6">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">7</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_orthogonale_unitaere_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_schmidtsche_orthonormierung'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_1_euclidean_vector_spaces_and_the_Rn_with_scalar_product/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_orthogonale_unitaere_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_schmidtsche_orthonormierung'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_1_euclidean_vector_spaces_and_the_Rn_with_scalar_product/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <data name="alpha">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-5</mn>
      </data>
      <data name="beta">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
      </data>
      <data name="gamma">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
      </data>
      <data name="delta">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">9</mn>
      </data>
      <data name="epsilon">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">9</mn>
      </data>
    </unit>
  </unit>
</data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_orthogonale_unitaere_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_schmidtsche_orthonormierung'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_1_euclidean_vector_spaces_and_the_Rn_with_scalar_product/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_orthogonale_unitaere_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_schmidtsche_orthonormierung'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_1_euclidean_vector_spaces_and_the_Rn_with_scalar_product/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">2</data></unit><unit name="subtask_1"><data name="vector_w1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="\\vec{w_1} = " mf:type="vector">
  <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><msqrt><mn mf:field="rational" mf:type="number">70</mn></msqrt></mrow><mrow><mn mf:field="rational" mf:type="number">21</mn></mrow></mfrac></mrow></mrow>
  <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mo>(</mo><mrow><mn mf:field="rational" mf:type="number">4</mn><mo>*</mo><msqrt><mn mf:field="rational" mf:type="number">70</mn></msqrt></mrow><mo>)</mo></mrow><mrow><mn mf:field="rational" mf:type="number">63</mn></mrow></mfrac></mrow></mrow>
</mrow></data><data name="vector_l2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="\\vec{l_2} = " mf:type="vector">
  <mrow mf:field="rational" mf:type="op-number"><mrow><mo>(</mo><mrow><msqrt><mn mf:field="rational" mf:type="number">70</mn></msqrt><mo>*</mo><mrow><mo>-</mo><mfrac><mn mf:field="int" mf:type="number">1</mn><mn mf:field="rational" mf:type="number">27</mn></mfrac></mrow></mrow><mo>)</mo><mo>+</mo><mo>(</mo><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">8</mn></mrow><mrow><mn mf:field="rational" mf:type="number">9</mn></mrow></mfrac></mrow><mo>)</mo></mrow></mrow>
  <mrow mf:field="rational" mf:type="op-number"><mrow><mo>(</mo><mrow><mo>-</mo><mrow><mfrac><mrow><mo>(</mo><mrow><mn mf:field="rational" mf:type="number">4</mn><mo>*</mo><msqrt><mn mf:field="rational" mf:type="number">70</mn></msqrt></mrow><mo>)</mo></mrow><mrow><mn mf:field="rational" mf:type="number">81</mn></mrow></mfrac></mrow></mrow><mo>)</mo><mo>+</mo><mo>(</mo><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">2</mn></mrow><mrow><mn mf:field="rational" mf:type="number">3</mn></mrow></mfrac></mrow><mo>)</mo></mrow></mrow>
</mrow></data><data name="vector_w2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="\\vec{w_2} = " mf:type="vector">
  <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">4</mn></mrow><mrow><mn mf:field="rational" mf:type="number">7</mn></mrow></mfrac></mrow></mrow>
  <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">3</mn></mrow><mrow><mn mf:field="rational" mf:type="number">7</mn></mrow></mfrac></mrow></mrow>
</mrow></data></unit><unit name="subtask_2"><data name="vector_w1b"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="\\vec{w_1} = " mf:type="vector">
  <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">1</mn></mrow><mrow><mn mf:field="rational" mf:type="number">5</mn></mrow></mfrac></mrow></mrow>
  <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">2</mn></mrow><mrow><mn mf:field="rational" mf:type="number">5</mn></mrow></mfrac></mrow></mrow>
  <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">2</mn></mrow><mrow><mn mf:field="rational" mf:type="number">5</mn></mrow></mfrac></mrow></mrow>
</mrow></data><data name="vector_l2b"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="\\vec{l_2} = " mf:type="vector">
  <mrow mf:field="rational" mf:type="op-number"><mn mf:field="rational" mf:type="number">1</mn></mrow>
  <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">1</mn></mrow><mrow><mn mf:field="rational" mf:type="number">2</mn></mrow></mfrac></mrow></mrow>
  <mrow mf:field="rational" mf:type="op-number"><mn mf:field="rational" mf:type="number">-1</mn></mrow>
</mrow></data><data name="vector_w2b"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="\\vec{w_2} = " mf:type="vector">
  <mrow mf:field="rational" mf:type="op-number"><msqrt><mn mf:field="rational" mf:type="number">2</mn></msqrt></mrow>
  <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><msqrt><mn mf:field="rational" mf:type="number">2</mn></msqrt></mrow><mrow><mn mf:field="rational" mf:type="number">2</mn></mrow></mfrac></mrow></mrow>
  <mrow mf:field="rational" mf:type="op-number"><mrow><mo>-</mo><msqrt><mn mf:field="rational" mf:type="number">2</mn></msqrt></mrow></mrow>
</mrow></data><data name="vector_l3b"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="\\vec{l_3} = " mf:type="vector">
  <mrow mf:field="rational" mf:type="op-number"><mrow><mo>(</mo><mrow><mfrac><mrow><mrow><mo>-</mo><msqrt><mn mf:field="rational" mf:type="number">10</mn></msqrt></mrow></mrow><mrow><mn mf:field="rational" mf:type="number">25</mn></mrow></mfrac></mrow><mo>)</mo><mo>+</mo><mo>(</mo><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">2</mn></mrow><mrow><mn mf:field="rational" mf:type="number">3</mn></mrow></mfrac></mrow><mo>)</mo></mrow></mrow>
  <mrow mf:field="rational" mf:type="op-number"><mrow><mo>(</mo><mrow><mn mf:field="rational" mf:type="number">-2</mn><mo>*</mo><msqrt><mn mf:field="rational" mf:type="number">10</mn></msqrt><mo>*</mo><mfrac><mn mf:field="int" mf:type="number">1</mn><mn mf:field="rational" mf:type="number">25</mn></mfrac></mrow><mo>)</mo><mo>-</mo><mo>(</mo><mrow><mfrac><mrow><mn mf:field="rational" mf:type="number">1</mn></mrow><mrow><mn mf:field="rational" mf:type="number">3</mn></mrow></mfrac></mrow><mo>)</mo></mrow></mrow>
  <mrow mf:field="rational" mf:type="op-number"><mrow><mo>(</mo><mrow><mo>-</mo><mrow><mfrac><mrow><mo>(</mo><mrow><mn mf:field="rational" mf:type="number">2</mn><mo>*</mo><msqrt><mn mf:field="rational" mf:type="number">10</mn></msqrt></mrow><mo>)</mo></mrow><mrow><mn mf:field="rational" mf:type="number">25</mn></mrow></mfrac></mrow></mrow><mo>)</mo><mo>+</mo><mn mf:field="rational" mf:type="number">1</mn></mrow></mrow>
</mrow></data><data name="vector_w3b"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="\\vec{w_3} = " mf:type="vector">
  <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mo>(</mo><mrow><mo>(</mo><mrow><mn mf:field="rational" mf:type="number">9</mn><mo>*</mo><msqrt><mn mf:field="rational" mf:type="number">10</mn></msqrt></mrow><mo>)</mo><mo>+</mo><mn mf:field="rational" mf:type="number">91</mn></mrow><mo>)</mo></mrow><mrow><mn mf:field="rational" mf:type="number">155</mn></mrow></mfrac></mrow></mrow>
  <mrow mf:field="rational" mf:type="op-number"><mrow><mrow><mo>-</mo><mrow><mo>(</mo><mrow><mn mf:field="rational" mf:type="number">39</mn><mo>*</mo><msqrt><mn mf:field="rational" mf:type="number">10</mn></msqrt></mrow><mo>)</mo><mo>+</mo><mn mf:field="rational" mf:type="number">136</mn></mrow></mrow><mo>*</mo><mfrac><mn mf:field="int" mf:type="number">1</mn><mn mf:field="rational" mf:type="number">310</mn></mfrac></mrow></mrow>
  <mrow mf:field="rational" mf:type="op-number"><mrow><mfrac><mrow><mo>(</mo><mrow><mn mf:field="rational" mf:type="number">3</mn><mo>*</mo><mo>(</mo><mrow><mo>(</mo><mrow><mn mf:field="rational" mf:type="number">7</mn><mo>*</mo><msqrt><mn mf:field="rational" mf:type="number">10</mn></msqrt></mrow><mo>)</mo><mo>+</mo><mn mf:field="rational" mf:type="number">88</mn></mrow><mo>)</mo></mrow><mo>)</mo></mrow><mrow><mn mf:field="rational" mf:type="number">310</mn></mrow></mfrac></mrow></mrow>
</mrow></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_orthogonale_unitaere_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_gram-schmidt_variante'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_1_euclidean_vector_spaces_and_the_Rn_with_scalar_product/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_orthogonale_unitaere_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_gram-schmidt_variante'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_1_euclidean_vector_spaces_and_the_Rn_with_scalar_product/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <data name="a">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
      </data>
      <data name="b">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
      </data>
      <data name="c">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
      </data>
    </unit>
  </unit>
</data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_orthogonale_unitaere_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_orthogonale_abbildung'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_2_orthogonal_and_symmetric_maps_and_matrices/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_orthogonale_unitaere_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_orthogonale_abbildung'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_2_orthogonal_and_symmetric_maps_and_matrices/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrix">
        <data name="component_11">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
        </data>
        <data name="component_21">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_orthogonale_unitaere_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_schrift_kapitel_08_09_ss_a'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_1_euclidean_vector_spaces_and_the_Rn_with_scalar_product/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_orthogonale_unitaere_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_schrift_kapitel_08_09_ss_a'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_1_euclidean_vector_spaces_and_the_Rn_with_scalar_product/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
</data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_vektorraeume_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_koordinaten_und_basen'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_koordinaten_und_basen'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector_1">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_vektorraeume_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_erzeugenden-system'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_erzeugenden-system'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="polynom_1">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
        </data>
        <data name="coefficient_0">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
        </data>
      </unit>
      <unit name="polynom_2">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
      </unit>
      <unit name="polynom_4">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
        </data>
      </unit>
      <unit name="polynom_6">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_vektorraeume_rn_cn_kn_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_vektorraum'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_rn_cn_kn_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_vektorraum'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="subtask_1">
        <unit name="vector_1">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">10</mn>
          </data>
        </unit>
        <unit name="vector_2">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
        </unit>
      </unit>
      <unit name="subtask_2">
        <unit name="vector_1">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
        </unit>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
        <unit name="vector_2">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-10</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_schrift_kapitel_05_ss_b'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_schrift_kapitel_05_ss_b'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
</data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_vr-operationen_in_cn'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_vr-operationen_in_cn'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="subtask_1">
        <unit name="vector_1">
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-4</mn>
          </data>
        </unit>
        <unit name="vector_2">
          <unit name="re-components">
            <data name="re-component_1">
              <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_vr-operationen_in_cn'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_vr-operationen_in_cn'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">5</data></unit><unit name="subtask_1"><data name="vsum1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex-rational" mf:type="vector">
  <mn mf:field="complex-rational" mf:type="number">3</mn>
  <mfrac mf:field="complex-rational" mf:type="number"><mn mf:field="int" mf:type="number">-13</mn><mn mf:field="int" mf:type="number">4</mn></mfrac>
</mrow></data></unit><unit name="subtask_2"><data name="vsum2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex-rational" mf:type="vector">
  <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" mf:field="complex-rational" mf:type="number"><mn mf:field="rational" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">7</mn><mn mf:field="rational" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">-2</mn></cnum>
  <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" mf:field="complex-rational" mf:type="number"><mn mf:field="rational" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">-6</mn><mn mf:field="rational" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">3</mn></cnum>
</mrow></data></unit><unit name="subtask_3"><data name="vsum3"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex-rational" mf:type="vector">
  <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" mf:field="complex-rational" mf:type="number"><mn mf:field="rational" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">-5</mn><mn mf:field="rational" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">-3</mn></cnum>
  <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" mf:field="complex-rational" mf:type="number"><mn mf:field="rational" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">5</mn><mn mf:field="rational" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">3</mn></cnum>
</mrow></data></unit><unit name="subtask_4"><data name="vsum4"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex-rational" mf:type="vector">
  <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" mf:field="complex-rational" mf:type="number"><mn mf:field="rational" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">6</mn><mn mf:field="rational" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">3</mn></cnum>
  <mn mf:field="complex-rational" mf:type="number">6</mn>
</mrow></data></unit><unit name="subtask_5"><data name="vsum5"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex-rational" mf:type="vector">
  <mn mf:field="complex-rational" mf:type="number">5</mn>
  <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" mf:field="complex-rational" mf:type="number"><mn mf:field="rational" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">4</mn><mn mf:field="rational" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">6</mn></cnum>
  <mn mf:field="complex-rational" mf:type="number">4</mn>
</mrow></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_linearkombination_in_r2'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_linearkombination_in_r2'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector_1">
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
        </data>
      </unit>
      <unit name="vector_2">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-4</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
        </data>
      </unit>
      <unit name="vector_3">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_linearkombination_in_r2'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_linearkombination_in_r2'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">1</data></unit><unit name="subtask_1"><data name="coeff1"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="1. Koeffizient: " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">-3</mn></mrow></data><data name="coeff2"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="2. Koeffizient: " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">-1</mn></mrow></data><data name="coeff3"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="3. Koeffizient: " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">-3</mn></mrow></data></unit></unit></unit></data_sheet>');

INSERT INTO anns_user_worksheet_generic_problem
  (the_user, ref, doc1, doc2, ann_type, content)
VALUES
  (
   -- the_user:
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_basen_in_rn'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_basen_in_rn'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vectors">
        <unit name="vector_1">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="component_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
        </unit>
        <unit name="vector_2">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
          <data name="component_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
          </data>
        </unit>
        <unit name="vector_3">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="component_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
        </unit>
        <unit name="vector_4">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
          <data name="component_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
        </unit>
        <unit name="vector_5">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="component_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
        </unit>
        <unit name="vector_6">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="component_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
        </unit>
        <unit name="vector_7">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="component_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
        </unit>
        <unit name="vector_8">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
          <data name="component_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
        </unit>
        <unit name="vector_9">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
          <data name="component_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
        </unit>
        <unit name="vector_10">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
          <data name="component_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
        </unit>
        <unit name="vector_11">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
          <data name="component_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
        </unit>
        <unit name="vector_12">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
          <data name="component_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
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
   (SELECT id FROM users WHERE login_name = 'student7'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_basen_in_rn'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_rn_cn_kn_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_basen_in_rn'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">2</data><unit name="subtask_1"><data name="selected_1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:type="vector">
          
            <mn mf:field="int" mf:type="number">3</mn>
          
            <mn mf:field="int" mf:type="number">3</mn>
          
            <mn mf:field="int" mf:type="number">1</mn>
          
        </mrow></data><data name="selected_2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:type="vector">
          
            <mn mf:field="int" mf:type="number">2</mn>
          
            <mn mf:field="int" mf:type="number">2</mn>
          
            <mn mf:field="int" mf:type="number">3</mn>
          
        </mrow></data><data name="selected_3"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:type="vector">
          
            <mn mf:field="int" mf:type="number">0</mn>
          
            <mn mf:field="int" mf:type="number">-2</mn>
          
            <mn mf:field="int" mf:type="number">-2</mn>
          
        </mrow></data></unit><unit name="subtask_2"><data name="selected_1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:type="vector">
          
            <mn mf:field="int" mf:type="number">2</mn>
          
            <mn mf:field="int" mf:type="number">2</mn>
          
            <mn mf:field="int" mf:type="number">1</mn>
          
        </mrow></data><data name="selected_2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:type="vector">
          
            <mn mf:field="int" mf:type="number">1</mn>
          
            <mn mf:field="int" mf:type="number">-1</mn>
          
            <mn mf:field="int" mf:type="number">2</mn>
          
        </mrow></data><data name="selected_3"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:type="vector">
          
            <mn mf:field="int" mf:type="number">3</mn>
          
            <mn mf:field="int" mf:type="number">0</mn>
          
            <mn mf:field="int" mf:type="number">3</mn>
          
        </mrow></data></unit></unit><unit name="subtask_1"><data name="basisExists"><mtext xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="string" mf:label="Basis des &#8477;^3 existiert: " mf:type="text" xmlns="http://www.w3.org/1998/Math/MathML">ja</mtext></data><data name="selected_1">common/problem/vectors/vector_1</data><data name="selected_2">common/problem/vectors/vector_7</data><data name="selected_3">common/problem/vectors/vector_12</data></unit><unit name="subtask_2"><data name="basisExists"><mtext xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="string" mf:label="Basis des &#8477;^3 existiert: " mf:type="text" xmlns="http://www.w3.org/1998/Math/MathML">ja</mtext></data><data name="selected_1">common/problem/vectors/vector_5</data><data name="selected_2">common/problem/vectors/vector_6</data><data name="selected_3">common/problem/vectors/vector_10</data></unit></unit></unit></data_sheet>');
