-- ===========================================================================
--Postgres sql
--Author: Fritz Lehmann-Grube, Tumult TU-Berlin, 2006
--$Id: path_for_section_id.sql,v 1.1 2006/09/05 14:38:37 rassy Exp $
--
--Description: 
-- 
--usage (e.g.):select id,path_for_section_id(contained_in) as "path",pure_name  from elements where id=100;
-- ===========================================================================

CREATE TABLE foo
(
  _count integer,
  _from integer references sections(id),
  _to integer references sections(id),
  _path text
);

CREATE OR REPLACE FUNCTION section_recursion(integer)
RETURNS SETOF foo
AS '
DECLARE
  count integer;
  tmp     integer;
  res     foo%ROWTYPE;
  path    text;
  delimiter text;
BEGIN
  count := 0;
  tmp := $1;
  path := '''';
  delimiter := ''/'';
  SELECT INTO res count, NULL, tmp, path;
  RETURN NEXT res;
  WHILE not(tmp = 0) LOOP
    count := count + 1;
    tmp := res._to;
    SELECT INTO res
      count AS "counter",
      tmp AS "_from",
      contained_in AS "_to",
      pure_name || path AS "_path"
    FROM sections
    WHERE id=tmp;
    path := delimiter || res._path;
  RETURN NEXT res;
  END LOOP;
  RETURN;
END;'
LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION path_for_section_id(integer)
RETURNS text
AS '
select _path from section_recursion($1) where _to=0;
'
LANGUAGE 'sql';
