-- Add margin threshold configurations to system_configs table
INSERT INTO system_configs (config_key, config_value, description, created_at, updated_at) 
VALUES 
('margin.threshold.red', '20.0', 'Red threshold for margin percentage (margin <= red is Red status)', NOW(), NOW()),
('margin.threshold.yellow', '30.0', 'Yellow threshold for margin percentage (red < margin <= yellow is Yellow status, margin > yellow is Green status)', NOW(), NOW())
ON CONFLICT (config_key) DO UPDATE
SET config_value = EXCLUDED.config_value,
    description = EXCLUDED.description,
    updated_at = NOW(); 