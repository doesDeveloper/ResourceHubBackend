ALTER TABLE resources
    ADD CONSTRAINT fk_resources_parent
    FOREIGN KEY (parent_uuid) REFERENCES resources(uuid)
    ON DELETE CASCADE;
