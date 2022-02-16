load data
CHARACTERSET ZHS16GBK
append
into table ytj_badfile_record
fields terminated by "@"
trailing nullcols 
(
acb_error_ytj_all_column char(100000) 
)
