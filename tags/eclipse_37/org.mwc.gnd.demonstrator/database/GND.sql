CREATE TABLE public.platforms (
       platformid SERIAL NOT NULL
     , platformname VARCHAR(255) NOT NULL
     , nationality VARCHAR(20)
     , designator VARCHAR(20)
     , color CHAR(6) DEFAULT 'FF0000'
     , PRIMARY KEY (platformid)
);

CREATE TABLE public.exercises (
       exerciseid SERIAL NOT NULL
     , exercisename VARCHAR(255) NOT NULL
     , description VARCHAR(255)
     , PRIMARY KEY (exerciseid)
);
CREATE UNIQUE INDEX ix_exercises_name ON public.exercises (exercisename);

CREATE TABLE public.formats (
       formatid SERIAL NOT NULL
     , formatname VARCHAR(255) NOT NULL
     , iconname VARCHAR(255)
     , hasSummary BOOL DEFAULT false
     , hasLocation BOOL DEFAULT false
     , PRIMARY KEY (formatid)
);
CREATE UNIQUE INDEX ix_sensortypes_name ON public.formats (formatname);

CREATE TABLE public.datasets (
       datasetid SERIAL NOT NULL
     , datasetname VARCHAR(255) NOT NULL
     , platformid INTEGER NOT NULL
     , exerciseid INTEGER
     , formatid INTEGER
     , created TIMESTAMP DEFAULT now() NOT NULL
     , startTime TIMESTAMP
     , endTime TIMESTAMP
     , PRIMARY KEY (datasetid)
     , CONSTRAINT FK_datasets_3 FOREIGN KEY (exerciseid)
                  REFERENCES public.exercises (exerciseid)
     , CONSTRAINT FK_datasets_4 FOREIGN KEY (platformid)
                  REFERENCES public.platforms (platformid)
     , CONSTRAINT FK_datasets_2 FOREIGN KEY (formatid)
                  REFERENCES public.formats (formatid) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE public.DataItems (
       itemid SERIAL NOT NULL
     , datasetid INTEGER NOT NULL
     , dtg TIMESTAMP
     , location POINT
     , summary VARCHAR(1000)
     , contenttype VARCHAR(255)
     , content TEXT
     , PRIMARY KEY (itemid)
     , CONSTRAINT fk_dataitem_1 FOREIGN KEY (datasetid)
                  REFERENCES public.datasets (datasetid) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX ix_dataitem_dtg ON public.DataItems (dtg);


-- switch the ownerships to the dev account
ALTER TABLE public.DataItems OWNER TO dev;
ALTER TABLE public.datasets OWNER TO dev;
ALTER TABLE public.formats OWNER TO dev;
ALTER TABLE public.exercises OWNER TO dev;
ALTER TABLE public.platforms OWNER TO dev;

-- we're going to add a postgis point into the column titled location
-- start off by ditching the existing (illustrative) one
ALTER TABLE dataitems DROP COLUMN location;

-- now do the PostGIS command
SELECT AddGeometryColumn('dataitems', 'location', 4326,'POINT',3);

-- we have to insert the view definition by hand, since the convenience function
-- only works for tables
INSERT INTO geometry_columns(  f_table_catalog, f_table_schema, f_table_name,f_geometry_column,  coord_dimension, srid, "type") 
    VALUES
      ('', 'public', 'datasetsview','the_geom', 2, 4326, 'LINESTRING');

-- now the view that translates a series of dataitems into a single line
CREATE OR REPLACE VIEW geo_dataset_line AS 
 SELECT st_makeline(dataitems.location) AS the_geom, dataitems.datasetid
   FROM dataitems
  GROUP BY dataitems.datasetid;
ALTER TABLE geo_dataset_line OWNER TO dev;

-- now the view that collates data for atom export
CREATE OR REPLACE VIEW geo_dataset AS 
 SELECT geo_dataset_line.datasetid AS oid, geo_dataset_line.datasetid, geo_dataset_line.the_geom, platforms.color, datasets.datasetname
   FROM geo_dataset_line, datasets, platforms
  WHERE geo_dataset_line.the_geom IS NOT NULL AND datasets.datasetid = geo_dataset_line.datasetid AND datasets.platformid = platforms.platformid
  ORDER BY datasets.datasetid;
ALTER TABLE geo_dataset OWNER TO dev;

CREATE OR REPLACE VIEW dataitemsview AS 
 SELECT datasets.datasetid, dataitems.location, platforms.color, datasets.datasetname
   FROM dataitems, datasets, platforms
  WHERE dataitems.datasetid = datasets.datasetid AND datasets.platformid = platforms.platformid
  ORDER BY datasets.datasetid, dataitems.itemid;
ALTER TABLE dataitemsview OWNER TO dev;

-- now the check data function that populates the dataset time periods
CREATE OR REPLACE FUNCTION checkdata()
  RETURNS integer AS
$BODY$
declare
	rec record;
	endVal timestamp;
	startVal timestamp;
begin
	for rec in select "datasetid"  from "datasets" loop
		select into startVal dtg from "dataitems" where "datasetid"=rec."datasetid" order by dtg limit 1; 
		select into endVal dtg from "dataitems" where "datasetid"=rec."datasetid" order by dtg desc limit 1; 
		update "datasets" set "starttime"=startVal, "endtime"=endVal where "datasetid"=rec."datasetid";
	end loop;
return 1;
end;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION checkdata() OWNER TO dev;

-- PUT IN SOME SAMPLE DATA
--
-- start off with the attributes
--
 insert into exercises (exerciseid, exercisename, description) values (2, 'Ferry demo ran on:15 May 2009 09:10:03 GMT','');
 insert into formats (formatid, formatname, iconname, hassummary, haslocation) values (2, 'data.pos', 'data.pos.png',	false, true);
 insert into platforms (platformid, platformname, nationality, designator, color) values (2, 'FISHER_1', '', '', 'FF0000');
--
-- now the dataset
--
 insert into datasets (datasetid, datasetname, platformid, exerciseid, formatid, created, starttime, endtime) values
(2,'SimRun dated:Fri May 15 10:10:10 BST 2009',2,2,2,'2009-05-15 10:10:10.863956','2003-01-24 13:00:18','2003-01-24 14:01:48');

--
-- and the data items themselves
--
insert into dataitems (itemid, datasetid, dtg, summary, contenttype, content, location) values 
(1,2,'2003-01-24 13:00:18','','application/vstatus+xml','<status course="44.0" speed="3.086666666666667 m/s" />','01010000A0E610000002C50F820AC04A402BB78CA4138039400000000000000080');
insert into dataitems (itemid, datasetid, dtg, summary, contenttype, content, location) values 
(2,2,'2003-01-24 13:01:18','','application/vstatus+xml','<status course="44.0" speed="3.086666666666667 m/s" />','01010000A0E61000000308608A34C04A40D493BF36628039400000000000000080');
insert into dataitems (itemid, datasetid, dtg, summary, contenttype, content, location) values 
(3,2,'2003-01-24 13:19:33','','application/vstatus+xml','<status course="44.0" speed="3.086666666666667 m/s" />','01010000A0E61000008913643229C34A40BE17537EE88539400000000000000080');

