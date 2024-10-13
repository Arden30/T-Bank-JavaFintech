package arden.java.kudago.repository;

import arden.java.kudago.repository.impl.StorageRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class StorageRepositoryTest {
    private StorageRepository<Long, String> storageRepository;

    @BeforeEach
    public void setUp() {
        storageRepository = new StorageRepositoryImpl<>();
    }

    @Test
    @DisplayName("Create: should add a new item to storage")
    public void create_ShouldAddItemToStorage() {
        String value = storageRepository.create(1L, "TestValue");

        assertThat(value).isEqualTo("TestValue");
        assertThat(storageRepository.read(1L)).isEqualTo("TestValue");
    }

    @Test
    @DisplayName("Create: should return null when key or value is null")
    public void create_ShouldReturnNull_WhenKeyOrValueIsNull() {
        assertThat(storageRepository.create(null, "TestValue")).isNull();
        assertThat(storageRepository.create(1L, null)).isNull();
    }

    @Test
    @DisplayName("Read: should return the item by key")
    public void read_ShouldReturnItemByKey() {
        storageRepository.create(1L, "TestValue");

        String result = storageRepository.read(1L);

        assertThat(result).isEqualTo("TestValue");
    }

    @Test
    @DisplayName("Read: should return null if key is null or not present")
    public void read_ShouldReturnNull_WhenKeyIsNullOrNotPresent() {
        assertThat(storageRepository.read(null)).isNull();
        assertThat(storageRepository.read(999L)).isNull();
    }

    @Test
    @DisplayName("ReadAll: should return all items in storage")
    public void readAll_ShouldReturnAllItems() {
        String first = storageRepository.create(1L, "Value1");
        String second = storageRepository.create(2L, "Value2");

        List<String> result = storageRepository.readAll();

        assertThat(result).isEqualTo(List.of(first, second));
    }

    @Test
    @DisplayName("Update: should update an existing item")
    public void update_ShouldUpdateItem() {
        storageRepository.create(1L, "InitialValue");

        String updatedValue = storageRepository.update(1L, "UpdatedValue");

        assertThat(updatedValue).isEqualTo("UpdatedValue");
        assertThat(storageRepository.read(1L)).isEqualTo("UpdatedValue");
    }

    @Test
    @DisplayName("Update: should return null if key or value is null")
    public void update_ShouldReturnNull_WhenKeyOrValueIsNull() {
        storageRepository.create(1L, "InitialValue");

        assertThat(storageRepository.update(null, "UpdatedValue")).isNull();
        assertThat(storageRepository.update(1L, null)).isNull();
    }

    @Test
    @DisplayName("Delete: should remove an item by key")
    public void delete_ShouldRemoveItemByKey() {
        storageRepository.create(1L, "ValueToDelete");

        boolean result = storageRepository.delete(1L);

        assertThat(result).isTrue();
        assertThat(storageRepository.read(1L)).isNull();
    }

    @Test
    @DisplayName("Delete: should return false if key is null or not present")
    public void delete_ShouldReturnFalse_WhenKeyIsNullOrNotPresent() {
        assertThat(storageRepository.delete(null)).isFalse();
        assertThat(storageRepository.delete(999L)).isFalse();
    }
}
