update census_daystats
  set sdate = sdate + ((select min(current_date - sdate) from census_daystats) - 1)
;
