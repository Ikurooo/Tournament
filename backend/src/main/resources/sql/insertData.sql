
// TODO temporary delete everything for testing

ALTER TABLE horse_tourney_linker DROP CONSTRAINT IF EXISTS horse_id;

DELETE FROM horse_tourney_linker WHERE horse_id < 0 OR tournament_id < 0;

DELETE FROM horse WHERE id < 0;

DELETE FROM tournament WHERE id < 0;

ALTER TABLE horse_tourney_linker ADD CONSTRAINT IF NOT EXISTS fk_horse_id FOREIGN KEY (horse_id) REFERENCES horse(id);


-- Füge Pferdedaten hinzu
INSERT INTO horse (id, name, sex, date_of_birth, height, weight, breed_id)
VALUES
    (-1, 'Wendy', 'FEMALE', '2019-08-05', 1.40, 380, -15),
    (-2, 'Hugo', 'MALE', '2020-02-20', 1.20, 320, -20),
    (-3, 'Bella', 'FEMALE', '2005-04-08', 1.45, 550, -1),
    (-4, 'Thunder', 'MALE', '2008-07-15', 1.60, 600, -2),
    (-5, 'Luna', 'FEMALE', '2012-11-22', 1.65, 650, -3),
    (-6, 'Apollo', 'MALE', '2003-09-03', 1.52, 500, -4),
    (-7, 'Sophie', 'FEMALE', '2010-06-18', 1.70, 700, -5),
    (-8, 'Max', 'MALE', '2006-03-27', 1.55, 580, -6),
    (-9, 'Bella', 'FEMALE', '2002-08-09', 1.48, 520, -7),
    (-10, 'Rocky', 'MALE', '2013-05-05', 1.55, 620, -8),
    (-11, 'Daisy', 'FEMALE', '2007-02-12', 1.30, 350, -9),
    (-12, 'Charlie', 'MALE', '2011-09-21', 1.68, 680, -10),
    (-13, 'Ruby', 'FEMALE', '2004-07-30', 1.10, 280, -11),
    (-14, 'Duke', 'MALE', '2009-03-14', 1.75, 800, -12),
    (-15, 'Rosie', 'FEMALE', '2001-12-08', 1.57, 590, -13),
    (-16, 'Jack', 'MALE', '2014-10-25', 1.52, 560, -14),
    (-17, 'Lilly', 'FEMALE', '2008-06-03', 1.40, 400, -15),
    (-18, 'Sam', 'MALE', '2010-01-17', 1.65, 650, -16),
    (-19, 'Misty', 'FEMALE', '2005-11-09', 1.25, 320, -17),
    (-20, 'Max', 'MALE', '2012-08-29', 1.72, 670, -18),
    (-21, 'Bella', 'FEMALE', '2003-07-06', 1.50, 580, -19),
    (-22, 'Rocky', 'MALE', '2007-04-12', 1.40, 450, -1),
    (-23, 'Misty', 'FEMALE', '2015-03-12', 1.32, 360, -7),
    (-24, 'Rocky', 'MALE', '2018-08-19', 1.42, 480, -6),
    (-25, 'Lucky', 'MALE', '2019-05-25', 1.58, 620, -5),
    (-26, 'Daisy', 'FEMALE', '2017-12-01', 1.28, 340, -9),
    (-27, 'Buddy', 'MALE', '2016-09-14', 1.68, 700, -10),
    (-28, 'Molly', 'FEMALE', '2014-04-03', 1.55, 580, -13),
    (-29, 'Cody', 'MALE', '2019-11-30', 1.45, 550, -2),
    (-30, 'Rosie', 'FEMALE', '2016-06-28', 1.52, 520, -14),
    (-31, 'Leo', 'MALE', '2017-03-05', 1.70, 720, -8),
    (-32, 'Luna', 'FEMALE', '2018-10-10', 1.62, 670, -19);

INSERT INTO tournament (id, name, start_date, end_date)
VALUES
    (-1, 'Rainbow Road', '2001-01-01', '2002-03-02'),
    (-2, 'Star Cup', '2003-05-15', '2004-07-20'),
    (-3, 'Mushroom Cup', '2005-09-10', '2006-11-25'),
    (-4, 'Flower Cup', '2007-01-30', '2008-04-12'),
    (-5, 'Special Cup', '2009-06-20', '2010-08-18'),
    (-6, 'Shell Cup', '2011-10-05', '2012-12-28'),
    (-7, 'Banana Cup', '2013-02-15', '2014-04-30'),
    (-8, 'Leaf Cup', '2015-06-25', '2016-08-22'),
    (-9, 'Lightning Cup', '2017-10-10', '2018-12-15'),
    (-10, 'Borderline Schizophrenic Cup', '1999-01-01', '2000-03-03');

INSERT INTO horse_tourney_linker (tournament_id, horse_id)
VALUES
    ( -1, -1 ),
    ( -1, -2 ),
    ( -1, -3 ),
    ( -1, -4 ),
    ( -1, -5 ),
    ( -1, -6 ),
    ( -1, -7 ),
    ( -1, -8 ),
    ( -2, -1 ),
    ( -9, -7 );


-- TODO: entry number and round reached don't match specification
INSERT INTO horse_tourney_linker (tournament_id, horse_id, round_reached, entry_number)
VALUES
    ( -10, -1, 3, 1 ), -- good
    ( -10, -2, 4, 2 ), -- good
    ( -10, -3, 1, 3 ), -- good
    ( -10, -4, 4, 4 ), -- good
    ( -10, -5, 2, 5 ), -- good
    ( -10, -6, 4, 6 ), -- good
    ( -10, -7, 3, 7 ), -- good
    ( -10, -8, 4, 8 ); -- good
