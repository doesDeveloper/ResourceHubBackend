ALTER TABLE file_metadata
    ADD CONSTRAINT fk_metadata_resource
    FOREIGN KEY (uuid) REFERENCES resources(uuid)
    ON DELETE CASCADE;