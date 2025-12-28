-- 1. Drop the existing constraint that is blocking the new values
ALTER TABLE investment DROP CONSTRAINT IF EXISTS investment_type_check;

-- 2. Add the updated constraint including the 3 new values (IJH, ACWI, FEZ)
ALTER TABLE investment ADD CONSTRAINT investment_type_check 
CHECK (type IN ('SP500', 'QQQ', 'IJH', 'ACWI', 'FEZ'));