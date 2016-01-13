/*--------------------------------------------------------
	MY_REFERENCES
siehe TEMPLATE1_README

usage:

psql template1 -f my_references.sql
--------------------------------------------------------*/

-- HILFSFUNKTION
-- DROP FUNCTION chunk(bytea,int,char);
CREATE OR REPLACE FUNCTION chunk(bytea,int,char) 
RETURNS bytea AS'
DECLARE res bytea;
argstring bytea;
len int;
bscount int;
charcount int; actchar int;
actsub bytea;
go boolean;
gogo boolean;
sep int; 
BEGIN
  argstring := $1;
  len := length(argstring);
--  RAISE NOTICE ''len is: % '',len;
  sep := hashbpchar($3);
--  RAISE NOTICE ''sep is: % '',sep;
  IF ( $2 NOT BETWEEN 1 AND 8 ) THEN 
    RAISE EXCEPTION ''OUT OF BOUNDS ! argument of function tgargs must be 1,..,8'';
    RETURN NULL;
  ELSIF length(argstring) < 3 THEN
    RAISE EXCEPTION ''argument % is not good'',argstring;
  ELSE
    res := substring(argstring,1,1);
--    RAISE NOTICE ''argument is: % '',argstring;
    bscount := 1;
    charcount := 0;
    WHILE bscount<$2 and len > charcount LOOP
      go := true;
      bscount := bscount + 1;
--      RAISE NOTICE ''bscount :%'',bscount;
      WHILE go LOOP
        actchar := get_byte(argstring,charcount);
        IF actchar = 0 THEN
          go := false;
        END IF;
--        RAISE NOTICE ''actual charnr is :%'',actchar;
        charcount := charcount + 1;
        IF len < charcount THEN go := false; END IF;
      END LOOP;
    END LOOP;
--    RAISE NOTICE ''WRITING'';
    gogo := false;
    IF len > charcount THEN 
      IF get_byte(argstring,charcount) != 0 THEN 
	IF get_byte(argstring,charcount) !=160 THEN
          gogo := true; 
	END IF;
      END IF;
    END IF;
    WHILE gogo = true LOOP
      actsub := substring(argstring,charcount+1,1);
--      RAISE NOTICE ''actual char is :%'',actsub;
      res := byteacat(res, actsub);
--      RAISE NOTICE ''actual result is :%'',res;
      charcount := charcount + 1;
      gogo := false; 
      IF len >= charcount THEN 
        IF get_byte(argstring,charcount) != 0 THEN 
          gogo := true; 
        END IF;
      END IF;
    END LOOP;
    RETURN substring(res,2);
  END IF;
  RETURN null;
END;'
LANGUAGE 'plpgsql';

--DROP VIEW my_references;
CREATE OR REPLACE VIEW my_references AS
SELECT 
a.tgrelid AS "FromOID", 
chunk(a.tgargs, 2, '//'::bpchar) AS "FromTable", 
chunk(a.tgargs, 5, '//'::bpchar) AS "FromCol", 
chunk(a.tgargs, 7, '//'::bpchar) AS "andFromCol", 
chunk(a.tgargs, 3, '//'::bpchar) AS "ToTable", 
chunk(a.tgargs, 6, '//'::bpchar) AS "ToCol", 
chunk(a.tgargs, 8, '//'::bpchar) AS "andToCol", 
a.tgconstrrelid AS "ToOID" 
FROM pg_trigger a, pg_trigger b, pg_trigger c 
WHERE (((((((a.tgtype = 21) 
AND (b.tgtype = 9)) 
AND (c.tgtype = 17)) 
AND (a.tgrelid = b.tgconstrrelid)) 
AND (a.tgrelid = c.tgconstrrelid)) 
AND (b.tgrelid = a.tgconstrrelid)) 
AND (c.tgrelid = a.tgconstrrelid));
