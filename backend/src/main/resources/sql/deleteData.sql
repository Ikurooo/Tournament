-- Drop foreign key constraint in horse_tourney_linker table
ALTER TABLE horse_tourney_linker DROP CONSTRAINT IF EXISTS horse_id;

-- Drop foreign key constraint in tournament table
ALTER TABLE horse_tourney_linker DROP CONSTRAINT IF EXISTS tournament_id;

-- Drop foreign key constraint in horse table
ALTER TABLE horse DROP CONSTRAINT IF EXISTS breed_id;



-- Delete records from horse_tourney_linker table first
DELETE FROM horse_tourney_linker;

-- Then delete records from tournament table
DELETE FROM tournament;

-- Then delete records from horse table
DELETE FROM horse;

-- Finally, delete records from breed table
DELETE FROM breed;
