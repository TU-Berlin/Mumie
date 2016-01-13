

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
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_1">
          <data name="coefficent_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-7</mn>
          </data>
          <data name="coefficent_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficent_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
          </data>
          <data name="coefficent_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-6</mn>
          </data>
          <data name="coefficent_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficent_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">8</mn>
          </data>
          <data name="coefficent_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficent_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="coefficent_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-9</mn>
          </data>
          <data name="coefficent_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="coefficent_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-10</mn>
          </data>
          <data name="coefficent_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">6</mn>
          </data>
          <data name="coefficent_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">9</mn>
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
   (SELECT id FROM users WHERE login_name = 'student1'),

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
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrix_A1">
        <data name="switch">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
        </data>
        <unit name="matrix1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_14">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficient_15">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
          <data name="coefficient_24">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_25">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
        </unit>
        <unit name="matrix2">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
        </unit>
      </unit>
      <unit name="matrix_A2">
        <data name="switch">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
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
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
          <data name="coefficient_25">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_35">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
        </unit>
        <unit name="matrix2">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="coefficient_33">
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
   (SELECT id FROM users WHERE login_name = 'student1'),

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
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">4</data></unit><unit name="subtask_4"><data name="solutionSelection"><mn xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:hidden="true" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">1</mn></data><data name="dummySelection"><mtext xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="string" mf:type="text" xmlns="http://www.w3.org/1998/Math/MathML">Das lineare Gleichungssystem hat keine Loesung.</mtext></data><data name="set"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="tuple-set" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:label="L = " mf:type="tuple-set"><mo>{</mo><mo>}</mo></mrow></data></unit></unit></unit></data_sheet>');

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
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrix">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-4</mn>
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
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="choices-3"><data name="choice-3">true</data><data name="choice-2">true</data><data name="choice-1">false</data></unit><unit name="choices-2"><data name="choice-3">true</data><data name="choice-2">true</data><data name="choice-1">false</data><data name="choice-4">false</data></unit><unit name="choices-1"><data name="choice-3">true</data><data name="choice-2">false</data><data name="choice-1">true</data><data name="choice-4">false</data></unit><unit name="choices-4"><data name="choice-3">true</data><data name="choice-2">false</data><data name="choice-1">true</data></unit></unit></unit></data_sheet>');



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
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-5</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-4</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-6</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-10</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-7</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
        </unit>
        <unit name="matrix_3">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-9</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-8</mn>
          </data>
        </unit>
        <unit name="matrix_4">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-6</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-10</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">7</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">8</mn>
          </data>
        </unit>
        <unit name="matrix_5">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">9</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">9</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
          </data>
        </unit>
        <unit name="matrix_6">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
        </unit>
        <unit name="matrix_7">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-8</mn>
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
   (SELECT id FROM users WHERE login_name = 'student1'),

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
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">4</data><unit name="subtask_2"><data name="selectedMatrix_1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:type="matrix">
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">1</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-1</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">5</mn>
          
            </mtd>
          </mtr>
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-5</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-4</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">5</mn>
          
            </mtd>
          </mtr>
        </mtable></data><data name="selectedMatrix_2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:type="matrix">
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-6</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">0</mn>
          
            </mtd>
          </mtr>
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">2</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-10</mn>
          
            </mtd>
          </mtr>
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">7</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">8</mn>
          
            </mtd>
          </mtr>
        </mtable></data></unit><unit name="subtask_3"><data name="selectedMatrix_1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:type="matrix">
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-6</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-10</mn>
          
            </mtd>
          </mtr>
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-7</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">1</mn>
          
            </mtd>
          </mtr>
        </mtable></data><data name="selectedMatrix_2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:type="matrix">
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-2</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">2</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">9</mn>
          
            </mtd>
          </mtr>
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">9</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">2</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-3</mn>
          
            </mtd>
          </mtr>
        </mtable></data></unit><unit name="subtask_4"><data name="selectedMatrix_1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:type="matrix">
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">3</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-9</mn>
          
            </mtd>
          </mtr>
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">5</mn>
          
            </mtd>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-8</mn>
          
            </mtd>
          </mtr>
        </mtable></data><data name="selectedMatrix_2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:type="matrix">
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">3</mn>
          
            </mtd>
          </mtr>
          <mtr>
            <mtd align="center">
              
            <mn mf:field="int" mf:type="number">-8</mn>
          
            </mtd>
          </mtr>
        </mtable></data></unit></unit><unit name="subtask_1"><data name="format_A1"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:label="Format von A_1:" mf:type="dimension" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="int" mf:type="number">2</mn>x<mn mf:field="int" mf:type="number">3</mn></mrow></data><data name="format_A2"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:label="Format von A_2:" mf:type="dimension" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="int" mf:type="number">2</mn>x<mn mf:field="int" mf:type="number">2</mn></mrow></data><data name="format_A3"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:label="Format von A_3:" mf:type="dimension" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="int" mf:type="number">2</mn>x<mn mf:field="int" mf:type="number">2</mn></mrow></data><data name="format_A4"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:label="Format von A_4:" mf:type="dimension" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="int" mf:type="number">3</mn>x<mn mf:field="int" mf:type="number">2</mn></mrow></data><data name="format_A5"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:label="Format von A_5:" mf:type="dimension" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="int" mf:type="number">2</mn>x<mn mf:field="int" mf:type="number">3</mn></mrow></data><data name="format_A6"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:label="Format von A_6:" mf:type="dimension" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="int" mf:type="number">1</mn>x<mn mf:field="int" mf:type="number">3</mn></mrow></data><data name="format_A7"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:label="Format von A_7:" mf:type="dimension" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="int" mf:type="number">2</mn>x<mn mf:field="int" mf:type="number">1</mn></mrow></data></unit><unit name="subtask_2"><data name="matrix_1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">-1</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">5</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">-5</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">-4</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">5</mn>
    </mtd>
  </mtr>
</mtable></data><data name="selectedMatrix_1">common/problem/matrices/matrix_1</data><data name="matrix_2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">-1</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">5</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">-5</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">-4</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">5</mn>
    </mtd>
  </mtr>
</mtable></data><data name="selectedMatrix_2">common/problem/matrices/matrix_4</data><data name="matrix_sum"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="Matrixprodukt: " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">27</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">50</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">57</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">80</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_3"><data name="matrix_1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">-6</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">-10</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">-7</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">1</mn>
    </mtd>
  </mtr>
</mtable></data><data name="selectedMatrix_1">common/problem/matrices/matrix_2</data><data name="matrix_2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">-6</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">-10</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">-7</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">1</mn>
    </mtd>
  </mtr>
</mtable></data><data name="selectedMatrix_2">common/problem/matrices/matrix_5</data><data name="matrix_sum"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="Matrixprodukt: " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-78</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-32</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-24</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">23</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-12</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-66</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_4"><data name="matrix_1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">3</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">-9</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">5</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">-8</mn>
    </mtd>
  </mtr>
</mtable></data><data name="selectedMatrix_1">common/problem/matrices/matrix_3</data><data name="matrix_2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">3</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">-9</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="real" mf:type="number">5</mn>
    </mtd>
    <mtd>
      <mn mf:field="real" mf:type="number">-8</mn>
    </mtd>
  </mtr>
</mtable></data><data name="selectedMatrix_2">common/problem/matrices/matrix_7</data><data name="matrix_sum"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="Matrixprodukt: " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">81</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">79</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-10</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">6</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-4</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-6</mn>
          </data>
        </unit>
        <unit name="matrix_3">
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-6</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">9</mn>
          </data>
        </unit>
        <unit name="matrix_5">
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">6</mn>
          </data>
        </unit>
        <unit name="matrix_4">
          <data name="coefficient_42">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
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
   (SELECT id FROM users WHERE login_name = 'student1'),

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
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">4</data></unit><unit name="subtask_1"><data name="matrix1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">-10</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-1</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-2</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">-1</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">5</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-2</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">-2</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-2</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">6</mn>
    </mtd>
  </mtr>
</mtable></data><data name="tmp1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">-10</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-1</mn>
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
      <mn mf:field="complex" mf:type="number">5</mn>
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
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">6</mn>
    </mtd>
  </mtr>
</mtable></data><data name="matrix2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">5</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-4</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">5</mn>
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
      <mn mf:field="complex" mf:type="number">-4</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">3</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-6</mn>
    </mtd>
  </mtr>
</mtable></data><data name="tmp2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:hidden="true" mf:type="matrix">
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
      <mn mf:field="complex" mf:type="number">5</mn>
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
      <mn mf:field="complex" mf:type="number">-4</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">3</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-6</mn>
    </mtd>
  </mtr>
</mtable></data><data name="matrix3"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">6</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">6</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">1</mn>
    </mtd>
  </mtr>
</mtable></data><data name="tmp3"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">6</mn>
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
</mtable></data></unit><unit name="subtask_2"><data name="matrix1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">-10</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-1</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-2</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">5</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-2</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">2</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">2</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">6</mn>
    </mtd>
  </mtr>
</mtable></data><data name="tmp1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">-10</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-1</mn>
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
      <mn mf:field="complex" mf:type="number">5</mn>
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
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">6</mn>
    </mtd>
  </mtr>
</mtable></data><data name="matrix2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-5</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">4</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">5</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-3</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">-4</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">3</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-6</mn>
    </mtd>
  </mtr>
</mtable></data><data name="tmp2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:hidden="true" mf:type="matrix">
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
      <mn mf:field="complex" mf:type="number">5</mn>
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
      <mn mf:field="complex" mf:type="number">-4</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">3</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-6</mn>
    </mtd>
  </mtr>
</mtable></data><data name="matrix3"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">6</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">-6</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">1</mn>
    </mtd>
  </mtr>
</mtable></data><data name="tmp3"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">6</mn>
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
</mtable></data></unit><unit name="subtask_3"><data name="matrix1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" mf:field="complex" mf:type="number"><mn mf:field="real" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">1</mn><mn mf:field="real" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">5</mn></cnum>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" mf:field="complex" mf:type="number"><mn mf:field="real" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">1</mn><mn mf:field="real" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">5</mn></cnum>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
  </mtr>
</mtable></data><data name="tmp1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" mf:field="complex" mf:type="number"><mn mf:field="real" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">1</mn><mn mf:field="real" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">5</mn></cnum>
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
</mtable></data></unit><unit name="subtask_4"><data name="matrix1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">-10</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-1</mn>
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
      <mn mf:field="complex" mf:type="number">5</mn>
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
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">6</mn>
    </mtd>
  </mtr>
</mtable></data><data name="tmp1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">-10</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-1</mn>
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
      <mn mf:field="complex" mf:type="number">5</mn>
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
      <mi mf:field="complex" mf:type="number"/>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">6</mn>
    </mtd>
  </mtr>
</mtable></data><data name="matrix2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">5</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">-4</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">3</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-6</mn>
    </mtd>
  </mtr>
</mtable></data><data name="tmp2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:hidden="true" mf:type="matrix">
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
      <mn mf:field="complex" mf:type="number">5</mn>
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
      <mn mf:field="complex" mf:type="number">-4</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">3</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">-6</mn>
    </mtd>
  </mtr>
</mtable></data><data name="matrix3"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">6</mn>
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
</mtable></data><data name="tmp3"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="complex" mf:type="number">6</mn>
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
</mtable></data><data name="matrix4"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" mf:field="complex" mf:type="number"><mn mf:field="real" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">1</mn><mn mf:field="real" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">5</mn></cnum>
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
</mtable></data><data name="tmp4"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="complex" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="complex" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <cnum xmlns="http://www.mumie.net/xml-namespace/mathml-ext" mf:field="complex" mf:type="number"><mn mf:field="real" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">1</mn><mn mf:field="real" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">5</mn></cnum>
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
   (SELECT id FROM users WHERE login_name = 'student1'),

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
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector_1">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
      </unit>
      <unit name="vector_2">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
        </data>
      </unit>
      <unit name="vector_3">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
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
   (SELECT id FROM users WHERE login_name = 'student1'),

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
   (SELECT id FROM ann_types WHERE name='problem_answers'),

E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">1</data></unit><unit name="subtask_1"><data name="matrix_a"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:label="A = " mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="rational" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">-1</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="rational" mf:type="number">-1</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">1</mn>
    </mtd>
  </mtr>
</mtable></data><data name="a_v1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:label="\\vec{v_1} = " mf:type="vector">
  <mn mf:field="rational" mf:type="number">3</mn>
  <mn mf:field="rational" mf:type="number">0</mn>
</mrow></data><data name="a_v2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:label="\\vec{v_2} = " mf:type="vector">
  <mn mf:field="rational" mf:type="number">0</mn>
  <mn mf:field="rational" mf:type="number">3</mn>
</mrow></data><data name="a_w1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:label="\\vec{w_1} = " mf:type="vector">
  <mn mf:field="rational" mf:type="number">3</mn>
  <mn mf:field="rational" mf:type="number">-3</mn>
</mrow></data><data name="a_w2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:label="\\vec{w_2} = " mf:type="vector">
  <mn mf:field="rational" mf:type="number">-3</mn>
  <mn mf:field="rational" mf:type="number">3</mn>
</mrow></data></unit></unit></unit></data_sheet>');



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
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="basis_1">
        <data name="factor_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
        </data>
        <data name="factor_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
        </data>
        <unit name="vector_1">
          <data name="coefficient_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="coefficient_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="coefficient_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
        </unit>
        <unit name="vector_2">
          <data name="coefficient_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="coefficient_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
          </data>
          <data name="coefficient_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
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
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
        <data name="factor_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
        </data>
        <unit name="vector_1">
          <data name="coefficient_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="coefficient_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="coefficient_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
          </data>
        </unit>
        <unit name="vector_2">
          <data name="coefficient_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="coefficient_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
          </data>
          <data name="coefficient_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
        </unit>
      </unit>
      <unit name="vector_m">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
        </data>
        <data name="coefficient_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
        </data>
      </unit>
      <unit name="basis_3">
        <data name="factor_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
        </data>
        <data name="factor_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
        <unit name="vector_1">
          <data name="coefficient_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficient_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
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
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
          <data name="coefficient_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
        </unit>
      </unit>
      <unit name="vector_p">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
        </data>
        <data name="coefficient_3">
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
   (SELECT id FROM users WHERE login_name = 'student1'),

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
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="polynom_1">
        <data name="coefficient_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
        </data>
        <data name="coefficient_0">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
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
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
        </data>
      </unit>
      <unit name="polynom_2">
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
        </data>
      </unit>
      <unit name="polynom_8">
        <data name="coefficient_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
        </data>
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
        </data>
      </unit>
      <unit name="polynom_3">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
        <data name="coefficient_0">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
        </data>
      </unit>
      <unit name="polynom_6">
        <data name="coefficient_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
        </data>
      </unit>
      <unit name="polynom_9">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
        </data>
        <data name="coefficient_0">
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
   (SELECT id FROM users WHERE login_name = 'student1'),

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
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-4</mn>
          </data>
        </unit>
        <unit name="matrix_3">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
        </unit>
        <unit name="matrix_4">
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
        </unit>
        <unit name="matrix_6">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-5</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-5</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
        </unit>
        <unit name="matrix_7">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
          </data>
        </unit>
        <unit name="matrix_8">
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-5</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
        </unit>
        <unit name="matrix_9">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-5</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
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
   (SELECT id FROM users WHERE login_name = 'student1'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_euklidische_unitaere_vektorraeume_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_standardskalarprodukt'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_1_euclidean_vector_spaces_and_the_Rn_with_scalar_product'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_euklidische_unitaere_vektorraeume_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_standardskalarprodukt'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/3_real_with_scalar_product/3_1_euclidean_vector_spaces_and_the_Rn_with_scalar_product')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector_a">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number">-1</mn>
        </data>
        <data name="component_2">
          <mfrac xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number"><mn mf:field="int" mf:type="number">-5</mn><mn mf:field="int" mf:type="number">2</mn></mfrac>
        </data>
      </unit>
      <unit name="vector_b">
        <data name="component_1">
          <mfrac xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number"><mn mf:field="int" mf:type="number">4</mn><mn mf:field="int" mf:type="number">3</mn></mfrac>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number">2</mn>
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
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector_a">
        <data name="component_1">
          <mfrac xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number"><mn mf:field="int" mf:type="number">-2</mn><mn mf:field="int" mf:type="number">3</mn></mfrac>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number">2</mn>
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
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_2">
          <data name="coefficent_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">6</mn>
          </data>
          <data name="coefficent_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
          </data>
          <data name="coefficent_14">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">15</mn>
          </data>
          <data name="b_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficent_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficent_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
          </data>
          <data name="coefficent_24">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">19</mn>
          </data>
          <data name="b_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
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
   (SELECT id FROM users WHERE login_name = 'student1'),

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
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">2</data></unit><unit name="subtask_1"><data name="matrix_a"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:label="Koeffizientenmatrix: " mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="rational" mf:type="number">6</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">4</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">15</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="rational" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">5</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">19</mn>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="matrix_b"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:label="erw. Koeffizientenmatrix: " mf:type="matrix">
  <mtr>
    <mtd>
      <mn mf:field="rational" mf:type="number">6</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">4</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">15</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">2</mn>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mn mf:field="rational" mf:type="number">1</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">0</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">5</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">19</mn>
    </mtd>
    <mtd>
      <mn mf:field="rational" mf:type="number">5</mn>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">3</data></unit><unit name="subtask_1"><data name="a_m1"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="m_1 = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">1</mn></mrow></data><data name="a_b1"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="n_1 = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">2</mn></mrow></data><data name="a_m2"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="m_2 = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">2</mn></mrow></data><data name="a_b2"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="n_2 = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">2</mn></mrow></data><data name="a_matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-1</mn></mrow>
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
</mtable></data><data name="a_vectorb"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:hidden="true" mf:type="matrix">
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
</mtable></data></unit><unit name="subtask_2"><data name="b_m1"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="m_1 = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">3</mn></mrow></data><data name="b_b1"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="n_1 = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">3</mn></mrow></data><data name="b_m2"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="m_2 = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">3</mn></mrow></data><data name="b_b2"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="n_2 = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">3</mn></mrow></data><data name="b_matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-3</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-3</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
  </mtr>
</mtable></data><data name="b_vectorb"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">3</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">3</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_3"><data name="c_m1"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="m_1 = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">5</mn></mrow></data><data name="c_b1"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="n_1 = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">3</mn></mrow></data><data name="c_m2"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="m_2 = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">5</mn></mrow></data><data name="c_b2"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="real" mf:label="n_2 = " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="real" mf:type="number">1</mn></mrow></data><data name="c_matrix"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-5</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-5</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
    </mtd>
  </mtr>
</mtable></data><data name="c_vectorb"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:hidden="true" mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">3</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
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
   (SELECT id FROM users WHERE login_name = 'student1'),

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
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="polynom">
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number">1</mn>
        </data>
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number">5</mn>
        </data>
        <data name="coefficient_0">
          <mfrac xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number"><mn mf:field="int" mf:type="number">2</mn><mn mf:field="int" mf:type="number">5</mn></mfrac>
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
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector">
        <data name="component_1">
          <mfrac xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number"><mn mf:field="int" mf:type="number">-3</mn><mn mf:field="int" mf:type="number">2</mn></mfrac>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="number">4</mn>
        </data>
      </unit>
      <unit name="basis_1">
        <data name="component_11">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
        </data>
        <data name="component_12">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
        <data name="component_21">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
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
   (SELECT id FROM users WHERE login_name = 'student1'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_lineare_abbildungen_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_kern_bild_zeilenextraktion'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_lineare_abbildungen_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_kern_bild_zeilenextraktion'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_2_linear_maps_and_matrices')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="common">
    <unit name="problem">
      <data name="coefficient">
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
   (SELECT id FROM users WHERE login_name = 'student1'),

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
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="common">
    <unit name="problem">
      <data name="coefficient">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
      </data>
      <data name="lambda">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">26</mn>
      </data>
    </unit>
  </unit>
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-4</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-5</mn>
          </data>
          <data name="coefficient_41">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_42">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="coefficient_43">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-4</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-5</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="coefficient_41">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_42">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="coefficient_43">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
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
   (SELECT id FROM users WHERE login_name = 'student1'),

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
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-8</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">8</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-5</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">6</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">6</mn>
          </data>
          <data name="coefficient_22">
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
   (SELECT id FROM users WHERE login_name = 'student1'),

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
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">1</data></unit><unit name="subtask_1"><data name="matrix_user"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="Produktmatrix: " mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">98</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-56</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">33</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-21</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">8</mn>
        </data>
        <data name="coefficient_3">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">7</mn>
        </data>
        <data name="coefficient_4">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">6</mn>
        </data>
        <data name="coefficient_5">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">7</mn>
        </data>
        <data name="coefficient_6">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">9</mn>
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
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">2</data></unit><unit name="subtask_1"><data name="matrixSum1"><mtable xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" class="bmatrix" mf:field="op-number" mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">2</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">4</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-16</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">12</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-7</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">14</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-1</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">21</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">13</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="matrixSum2"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:type="matrix">
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">3,5</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">12</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">4</mn></mrow>
    </mtd>
  </mtr>
  <mtr>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-1</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-20,75</mn></mrow>
    </mtd>
    <mtd>
      <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">-10</mn></mrow>
    </mtd>
  </mtr>
</mtable></data></unit></unit></unit></data_sheet>');

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
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector_1">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-4</mn>
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
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   E'<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">2</data></unit><unit name="subtask_1"><data name="coordsB1vector1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="\\vec{v_B}$noc[ =]" mf:type="vector">
  <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
  <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
</mrow></data><data name="vector_1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:label="\\vec{v}$noc[ =]" mf:type="vector">
  <mn mf:field="rational" mf:type="number">2</mn>
  <mn mf:field="rational" mf:type="number">0</mn>
</mrow></data></unit><unit name="subtask_2"><data name="coordsB2vector1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="op-number" mf:label="\\vec{v_B}$noc[ =]" mf:type="vector">
  <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">2</mn></mrow>
  <mrow mf:field="real" mf:type="op-number"><mn mf:field="real" mf:type="number">1</mn></mrow>
</mrow></data><data name="vector_1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:label="\\vec{v}$noc[ =]" mf:type="vector">
  <mn mf:field="rational" mf:type="number">2</mn>
  <mn mf:field="rational" mf:type="number">4</mn>
</mrow></data></unit></unit></unit></data_sheet>');

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
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="polynom_1">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
        </data>
        <data name="coefficient_0">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">6</mn>
        </data>
      </unit>
      <unit name="polynom_2">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
      </unit>
      <unit name="polynom_4">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
        </data>
      </unit>
      <unit name="polynom_6">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-5</mn>
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
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">1</data><unit name="subtask_1"><data name="selected_1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="op-number">
                    
          <mn mf:field="int" mf:type="number">1</mn>
        
                    <mi>x</mi>
                  </mrow></data><data name="selected_2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="op-number">
                        
          <mn mf:field="int" mf:type="number">5</mn>
        
                        <mi>x</mi>
                      </mrow></data><data name="selected_3"><mrow xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="op-number">
                          <mn mf:field="int" mf:type="number">-1</mn><mo>*</mo>
          <mn mf:field="int" mf:type="number">2</mn>
        
                          <mi>x</mi>
                          <mo>-</mo>
          <mn mf:field="int" mf:type="number">6</mn>
        
                        </mrow></data></unit></unit><unit name="subtask_1"><data name="selected_1">common/problem/polynom_2</data><data name="selected_2">common/problem/polynom_4</data><data name="selected_3">common/problem/polynom_5</data><data name="nrOfElements"><mn xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:hidden="true" mf:type="number" xmlns="http://www.w3.org/1998/Math/MathML">3</mn></data></unit></unit></unit></data_sheet>');

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
       WHERE pure_name='wks_vektorraeume_rn_cn_kn_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_linearkombination'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_rn_cn_kn_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_linearkombination'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector_1">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
      </unit>
      <unit name="vector_2">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
        </data>
      </unit>
      <data name="coeff_1">
        <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
      </data>
      <data name="coeff_2">
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
   (SELECT id FROM users WHERE login_name = 'student1'),

   -- ref:
   (SELECT id FROM refs_worksheet_generic_problem
    WHERE from_doc =
      (SELECT id FROM worksheets
       WHERE pure_name='wks_vektorraeume_rn_cn_kn_prl'
       AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prl_linearkombination'
       AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_vektorraeume_rn_cn_kn_prl'
    AND contained_in=section_id_for_path(0, 'org/tub/ss_09/courses/lineare_algebra_ss_09')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prl_linearkombination'
    AND contained_in=section_id_for_path(0, 'content/tub/linear_algebra/problems/1_no_geometric_structure/1_1_vector_spaces_and_the_Kn/')),

   -- ann_type:
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">1</data></unit><unit name="subtask_1"><data name="alpha_1"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:label="1. Koeffizient: " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="rational" mf:type="number">-2</mn></mrow></data><data name="alpha_2"><mrow xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:label="2. Koeffizient: " mf:type="op-number" xmlns="http://www.w3.org/1998/Math/MathML"><mn mf:field="rational" mf:type="number">2</mn></mrow></data></unit></unit></unit></data_sheet>');

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
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="subtask_1">
        <unit name="vector_1">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
          </data>
        </unit>
        <unit name="vector_2">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">5</mn>
          </data>
        </unit>
      </unit>
      <unit name="subtask_2">
        <unit name="vector_1">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-7</mn>
          </data>
        </unit>
        <data name="coefficient_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
        </data>
        <unit name="vector_2">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
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
   (SELECT id FROM users WHERE login_name = 'student1'),

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
   (SELECT id FROM ann_types WHERE name='problem_answers'),

   -- content:
   '<?xml version="1.0" encoding="ASCII" standalone="no"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="generic"><data name="problemType">default</data><data name="currentSubtask">1</data></unit><unit name="subtask_1"><data name="vec1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="rational" mf:type="vector">
  <mn mf:field="rational" mf:type="number">2</mn>
  <mfrac mf:field="rational" mf:type="number"><mn mf:field="int" mf:type="number">15</mn><mn mf:field="int" mf:type="number">4</mn></mfrac>
</mrow></data></unit></unit></unit></data_sheet>');

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
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="subtask_1">
        <unit name="vector_1">
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">4</mn>
          </data>
        </unit>
        <unit name="vector_2">
          <unit name="re-components">
            <data name="re-component_1">
              <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-4</mn>
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
   (SELECT id FROM users WHERE login_name = 'student1'),

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
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vector_1">
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
        </data>
      </unit>
      <unit name="vector_2">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
        </data>
      </unit>
      <unit name="vector_3">
        <data name="component_1">
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
   (SELECT id FROM users WHERE login_name = 'student1'),

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
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="vectors">
        <unit name="vector_1">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="component_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
        </unit>
        <unit name="vector_2">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="component_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
        </unit>
        <unit name="vector_3">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="component_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
        </unit>
        <unit name="vector_4">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
          <data name="component_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
        </unit>
        <unit name="vector_5">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="component_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
        </unit>
        <unit name="vector_6">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="component_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
        </unit>
        <unit name="vector_7">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
          </data>
          <data name="component_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-2</mn>
          </data>
        </unit>
        <unit name="vector_8">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="component_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
        </unit>
        <unit name="vector_9">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-3</mn>
          </data>
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">1</mn>
          </data>
          <data name="component_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
        </unit>
        <unit name="vector_10">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">2</mn>
          </data>
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
          <data name="component_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">0</mn>
          </data>
        </unit>
        <unit name="vector_11">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="component_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
        </unit>
        <unit name="vector_12">
          <data name="component_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="component_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">3</mn>
          </data>
          <data name="component_3">
            <mn xmlns="http://www.w3.org/1998/Math/MathML" xmlns:mf="http://www.mumie.net/xml-namespace/exercise-object-attributes" mf:field="int" mf:type="number">-1</mn>
          </data>
        </unit>
      </unit>
    </unit>
  </unit>
</data_sheet>');
