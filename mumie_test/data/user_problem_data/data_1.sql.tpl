
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
          <mn xmlns="http://www.w3.org/1998/Math/MathML">-2</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">-3</mn>
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
          <mn xmlns="http://www.w3.org/1998/Math/MathML">0</mn>
        </data>
        <data name="coefficient_12">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">4</mn>
        </data>
        <data name="coefficient_14">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">-5</mn>
        </data>
        <data name="coefficient_15">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">-5</mn>
        </data>
        <data name="coefficient_21">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">3</mn>
        </data>
        <data name="coefficient_22">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">-1</mn>
        </data>
        <data name="coefficient_24">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">2</mn>
        </data>
        <data name="coefficient_25">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">-2</mn>
        </data>
        <data name="coefficient_31">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
        </data>
        <data name="coefficient_32">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">-2</mn>
        </data>
        <data name="coefficient_34">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">0</mn>
        </data>
        <data name="coefficient_35">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">0</mn>
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
   (SELECT id FROM ann_types WHERE name='personalized_problem_data'),

   -- content:
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet">
  <unit name="user">
    <unit name="problem">
      <unit name="matrices">
        <unit name="matrix_S">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-4</mn>
          </data>
          <data name="coefficient_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">4</mn>
          </data>
          <data name="coefficient_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-5</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">4</mn>
          </data>
          <data name="coefficient_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-1</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">4</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-5</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
          </data>
        </unit>
        <unit name="matrix_D">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">0</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
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
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-1</mn>
          </data>
          <data name="coefficent_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">5</mn>
          </data>
          <data name="coefficent_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">3</mn>
          </data>
          <data name="coefficent_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-4</mn>
          </data>
          <data name="coefficent_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-8</mn>
          </data>
        </unit>
        <unit name="matrix_2">
          <data name="coefficent_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">6</mn>
          </data>
          <data name="coefficent_12">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">3</mn>
          </data>
          <data name="coefficent_13">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-3</mn>
          </data>
          <data name="coefficent_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-5</mn>
          </data>
          <data name="coefficent_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
          </data>
          <data name="coefficent_23">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-9</mn>
          </data>
          <data name="coefficent_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">3</mn>
          </data>
          <data name="coefficent_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">7</mn>
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
          <mn xmlns="http://www.w3.org/1998/Math/MathML">2</mn>
        </data>
        <unit name="matrix1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
          </data>
          <data name="coefficient_14">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
          </data>
          <data name="coefficient_15">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-2</mn>
          </data>
          <data name="coefficient_24">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-1</mn>
          </data>
          <data name="coefficient_25">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-1</mn>
          </data>
        </unit>
        <unit name="matrix2">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">2</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-1</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-2</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
          </data>
          <data name="coefficient_33">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-2</mn>
          </data>
        </unit>
      </unit>
      <unit name="matrix_A2">
        <data name="switch">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">2</mn>
        </data>
        <unit name="matrix1">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
          </data>
          <data name="coefficient_14">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
          </data>
          <data name="coefficient_15">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
          </data>
          <data name="coefficient_25">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">2</mn>
          </data>
          <data name="coefficient_35">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
          </data>
        </unit>
        <unit name="matrix2">
          <data name="coefficient_11">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
          </data>
          <data name="coefficient_21">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">2</mn>
          </data>
          <data name="coefficient_22">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-1</mn>
          </data>
          <data name="coefficient_31">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">2</mn>
          </data>
          <data name="coefficient_32">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">2</mn>
          </data>
          <data name="coefficient_33">
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
          <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
        </data>
        <data name="coefficient">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">-4</mn>
        </data>
      </unit>
      <unit name="vector_a">
        <data name="component_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">-2</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
        </data>
        <data name="component_3">
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
            <mn xmlns="http://www.w3.org/1998/Math/MathML">2</mn>
          </data>
          <data name="coefficint_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">3</mn>
          </data>
          <data name="coefficint_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">6</mn>
          </data>
        </unit>
        <unit name="polynom_2">
          <data name="coefficint_1">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">4</mn>
          </data>
          <data name="coefficint_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">5</mn>
          </data>
        </unit>
        <unit name="polynom_3">
          <data name="coefficint_2">
            <mn xmlns="http://www.w3.org/1998/Math/MathML">-2</mn>
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
       WHERE pure_name='wks_orthogonale_unitaere_abbildungen_hwk'
       AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08'))
    AND to_doc =
      (SELECT id FROM generic_problems
       WHERE pure_name='g_prb_klausuraufgabeWS2006R2'
       AND contained_in=section_id_for_path(0, 'content/lineare_algebra/aufgaben/orthogonale_unitaere_abbildungen'))),

   -- doc1:
   (SELECT id FROM worksheets
    WHERE pure_name='wks_orthogonale_unitaere_abbildungen_hwk'
    AND contained_in=section_id_for_path(0, 'org/tub/ws_07_08/courses/lineare_algebra_ws_07_08')),

   -- doc2:
   (SELECT id FROM generic_problems
    WHERE pure_name='g_prb_klausuraufgabeWS2006R2'
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
          <mn xmlns="http://www.w3.org/1998/Math/MathML">-5</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">-2</mn>
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
        <mn xmlns="http://www.w3.org/1998/Math/MathML">964</mn>
      </data>
      <data name="D">
        <mn xmlns="http://www.w3.org/1998/Math/MathML">10</mn>
      </data>
      <data name="L">
        <mn xmlns="http://www.w3.org/1998/Math/MathML">26</mn>
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
          <mn xmlns="http://www.w3.org/1998/Math/MathML">2</mn>
        </data>
        <data name="coefficient_0">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">6</mn>
        </data>
      </unit>
      <unit name="polynom_2">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">3</mn>
        </data>
      </unit>
      <unit name="polynom_4">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">4</mn>
        </data>
      </unit>
      <unit name="polynom_6">
        <data name="coefficient_1">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">-3</mn>
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
          <mn xmlns="http://www.w3.org/1998/Math/MathML">-1</mn>
        </data>
        <data name="coefficient_2">
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
          <mn xmlns="http://www.w3.org/1998/Math/MathML">-4</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">2</mn>
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
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="matrixx"><mtable xmlns="http://www.w3.org/1998/Math/MathML" class="bmatrix" field="op-number" type="matrix">
  <mtr>
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
  </mtr>
</mtable></data></unit><unit name="subtask_2"><data name="x1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-number" type="vector">
  <mi field="real" type="op-number"/>
  <mi field="real" type="op-number"/>
</mrow></data><data name="x2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-number" type="vector">
  <mi field="real" type="op-number"/>
  <mi field="real" type="op-number"/>
</mrow></data><data name="l1"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="real" type="op-number"><mn field="real" type="number">1</mn></mrow></data><data name="l2"><mrow xmlns="http://www.w3.org/1998/Math/MathML" field="real" type="op-number"><mn field="real" type="number">1</mn></mrow></data></unit></unit></unit></data_sheet>');

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
   '<?xml version="1.0" encoding="ASCII"?><data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"><unit name="user"><unit name="answer"><unit name="subtask_1"><data name="p"><mrow xmlns="http://www.w3.org/1998/Math/MathML" class="column-vector" field="op-number" type="vector">
  <mi field="real" type="op-number"/>
  <mi field="real" type="op-number"/>
  <mi field="real" type="op-number"/>
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
          <mn xmlns="http://www.w3.org/1998/Math/MathML">-3</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">3</mn>
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
        <mn xmlns="http://www.w3.org/1998/Math/MathML">8</mn>
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
          <mn xmlns="http://www.w3.org/1998/Math/MathML">2</mn>
        </data>
        <data name="component_2">
          <mn xmlns="http://www.w3.org/1998/Math/MathML">1</mn>
        </data>
      </unit>
    </unit>
  </unit>
</data_sheet>
');
