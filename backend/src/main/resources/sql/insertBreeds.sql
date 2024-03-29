MERGE INTO breed AS target
    USING (VALUES
               (-1, 'Andalusian'),
               (-2, 'Appaloosa'),
               (-3, 'Arabian'),
               (-4, 'Belgian Draft'),
               (-5, 'Connemara Pony'),
               (-6, 'Dartmoor Pony'),
               (-7, 'Friesian'),
               (-8, 'Haflinger'),
               (-9, 'Hanoverian'),
               (-10, 'Icelandic Horse'),
               (-11, 'Lipizzaner'),
               (-12, 'Oldenburg'),
               (-13, 'Paint Horse'),
               (-14, 'Quarter Horse'),
               (-15, 'Shetland Pony'),
               (-16, 'Tinker'),
               (-17, 'Trakehner'),
               (-18, 'Warmblood'),
               (-19, 'Welsh Cob'),
               (-20, 'Welsh Pony')
        ) AS source (id, name)
ON target.id = source.id
WHEN NOT MATCHED THEN
    INSERT (id, name) VALUES (source.id, source.name);