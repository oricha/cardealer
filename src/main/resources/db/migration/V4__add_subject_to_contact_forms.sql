-- Add subject column to contact_forms table to match ContactForm entity
ALTER TABLE contact_forms
ADD COLUMN IF NOT EXISTS subject VARCHAR(150);

UPDATE contact_forms
SET subject = 'Consulta general'
WHERE subject IS NULL;

ALTER TABLE contact_forms
ALTER COLUMN subject SET NOT NULL;
