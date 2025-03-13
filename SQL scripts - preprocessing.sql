USE gtfs

DROP TABLE agency, calendar_dates, feed_info, transfers;

ALTER TABLE stops 
DROP COLUMN stop_code;

ALTER TABLE stops 
DROP COLUMN parent_station;

ALTER TABLE stops 
DROP COLUMN stop_timezone;

ALTER TABLE stops 
DROP COLUMN wheelchair_boarding;

ALTER TABLE stops 
DROP COLUMN platform_code;

ALTER TABLE stops 
DROP COLUMN zone_id;

ALTER TABLE stop_times 
DROP COLUMN stop_headsign;

ALTER TABLE stop_times 
DROP COLUMN pickup_type;

ALTER TABLE stop_times 
DROP COLUMN drop_off_type;

ALTER TABLE stop_times 
DROP COLUMN timepoint;

ALTER TABLE stop_times 
DROP COLUMN fare_units_traveled;

ALTER TABLE trips 
DROP COLUMN service_id;

ALTER TABLE trips 
DROP COLUMN realtime_trip_id;

ALTER TABLE trips 
DROP COLUMN direction_id;

ALTER TABLE trips 
DROP COLUMN block_id;

ALTER TABLE trips 
DROP COLUMN wheelchair_accessible;

ALTER TABLE trips 
DROP COLUMN bikes_allowed;

ALTER TABLE routes 
DROP COLUMN route_desc;

ALTER TABLE routes 
DROP COLUMN route_color;

ALTER TABLE routes 
DROP COLUMN route_text_color;

ALTER TABLE routes 
DROP COLUMN route_url;

ALTER TABLE stops
ADD PRIMARY KEY (stop_id); 

ALTER TABLE stop_times
ADD PRIMARY KEY (trip_id, stop_sequence);

ALTER TABLE trips
ADD PRIMARY KEY (trip_id); 

ALTER TABLE routes
ADD PRIMARY KEY (route_id); 

ALTER TABLE shapes
ADD PRIMARY KEY (shape_id, shape_pt_sequence);

ALTER TABLE stop_times
ADD FOREIGN KEY (trip_id) REFERENCES trips(trip_id)
on delete CASCADE;

ALTER TABLE trips
ADD FOREIGN KEY (route_id) REFERENCES routes(route_id)
on delete CASCADE; 

delete from stops 
where not (stop_lat >= '50.803773' AND stop_lon >= '5.638556' AND stop_lat <= '50.911833' AND stop_lon <= '5.763045');

delete from stops
where location_type <> 0;

alter table stops 
drop column location_type;

alter table stops 
modify column stop_id  int;

delete from stop_times 
where stop_id not in (select s.stop_id from stops s);

ALTER TABLE stop_times
ADD FOREIGN KEY (stop_id) REFERENCES stops(stop_id)
on delete CASCADE; 

delete from trips
where trip_id not in (select t.trip_id from stop_times t);

delete from routes
where route_id not in (select r.route_id from trips r);

delete from routes
where route_type <> 3;

alter table routes 
drop column route_type;

delete from routes
where agency_id
not like 'ARR';

delete from shapes 
where shape_id not in (select sh.shape_id from trips sh);