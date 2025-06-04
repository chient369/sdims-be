-- Add team_id column to employees table
ALTER TABLE employees
ADD COLUMN team_id BIGINT;

-- Add foreign key constraint
ALTER TABLE employees
ADD CONSTRAINT fk_employees_team
FOREIGN KEY (team_id) REFERENCES teams(id);

-- Migrate data from team column to team_id
-- This will link employees to teams based on team name
UPDATE employees e
SET team_id = (SELECT id FROM teams t WHERE t.name = e.team)
WHERE e.team IS NOT NULL;

-- Note: After confirming the migration is successful, 
-- in a future migration, we will remove the team column 