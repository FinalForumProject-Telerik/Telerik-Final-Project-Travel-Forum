// java/com/example/forum/services/UserServiceImplTests.java
package com.example.forum.services;

import com.example.forum.exceptions.AuthorizationException;
import com.example.forum.exceptions.EntityDuplicateException;
import com.example.forum.exceptions.EntityNotFoundException;
import com.example.forum.helpers.UserMapper;
import com.example.forum.models.User;
import com.example.forum.models.dto.UserDto;
import com.example.forum.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {

    @Mock
    UserRepository mockRepository;

    @Mock
    UserMapper mockMapper;

    @InjectMocks
    UserServiceImpl service;

    @Test
    void get_Should_ReturnAllUsers_When_RequesterIsAdmin() {
        // Arrange
        User admin = new User();
        admin.setAdmin(true);

        User mockUser1 = new User();
        User mockUser2 = new User();
        List<User> users = Arrays.asList(mockUser1, mockUser2);

        when(mockRepository.get()).thenReturn(users);

        // Act
        List<User> result = service.get(admin);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(mockUser1));
        assertTrue(result.contains(mockUser2));
        verify(mockRepository, times(1)).get();
    }

    @Test
    void get_Should_ThrowAuthorizationException_When_NotAdmin() {
        // Arrange
        User requester = new User();
        requester.setAdmin(false);

        // Act & Assert
        assertThrows(AuthorizationException.class, () -> service.get(requester));
        verifyNoInteractions(mockRepository);
    }

    @Test
    void getByEmail_Should_Return_User_From_Repository() {
        // Arrange
        String email = "mock@mail.com";
        User mockUser = new User();
        when(mockRepository.getByEmail(email)).thenReturn(mockUser);

        // Act
        User result = service.getByEmail(email);

        // Assert - repository result returned
        assertEquals(mockUser, result);
        verify(mockRepository, times(1)).getByEmail(email);
    }

    @Test
    void getByUsername_Should_Return_User_From_Repository() {
        // Arrange
        String username = "MockUser";
        User mockUser = new User();
        when(mockRepository.getByUsername(username)).thenReturn(mockUser);

        // Act
        User result = service.getByUsername(username);

        // Assert
        assertEquals(mockUser, result);
        verify(mockRepository, times(1)).getByUsername(username);
    }

    @Test
    void create_Should_Throw_When_UsernameExists() {
        // Arrange
        User toCreate = new User();
        toCreate.setUsername("exist");
        User existing = new User();
        when(mockRepository.getByUsername("exist")).thenReturn(existing);

        // Act & Assert
        assertThrows(EntityDuplicateException.class, () -> service.create(toCreate));
        verify(mockRepository, times(1)).getByUsername("exist");
        verify(mockRepository, never()).create(any());
    }

    @Test
    void create_Should_Create_When_UsernameDoesNotExist() {
        // Arrange
        User toCreate = new User();
        toCreate.setUsername("mockuser");

        when(mockRepository.getByUsername("mockuser")).thenThrow(new EntityNotFoundException("User", "username", "mockuser"));

        // Act
        service.create(toCreate);

        // Assert
        verify(mockRepository, times(1)).getByUsername("mockuser");
        verify(mockRepository, times(1)).create(toCreate);
    }

    @Test
    void blockUser_Should_Throw_When_RequesterNotAdmin() {
        // Arrange
        User requester = new User();
        requester.setAdmin(false);

        // Act & Assert
        assertThrows(AuthorizationException.class, () -> service.blockUser(1, requester));
        verifyNoInteractions(mockRepository);
    }

    @Test
    void blockUser_Should_BlockUser_When_RequesterIsAdmin() {
        // Arrange
        User requester = new User();
        requester.setAdmin(true);

        User target = new User();
        target.setBlocked(false);
        when(mockRepository.get(5)).thenReturn(target);

        // Act
        service.blockUser(5, requester);

        // Assert
        assertTrue(target.isBlocked());
        verify(mockRepository, times(1)).get(5);
        verify(mockRepository, times(1)).update(target);
    }

    @Test
    void unblockUser_Should_UnblockUser_When_RequesterIsAdmin() {
        // Arrange
        User requester = new User();
        requester.setAdmin(true);

        User target = new User();
        target.setBlocked(true);
        when(mockRepository.get(7)).thenReturn(target);

        // Act
        service.unblockUser(7, requester);

        // Assert
        assertFalse(target.isBlocked());
        verify(mockRepository, times(1)).get(7);
        verify(mockRepository, times(1)).update(target);
    }

    @Test
    void getUsers_Should_Map_Users_To_Dtos() {
        // Arrange
        User mockUser1 = new User();
        User mockUser2 = new User();
        UserDto mockDTO1 = new UserDto();
        UserDto mockDTO2 = new UserDto();

        when(mockRepository.get()).thenReturn(Arrays.asList(mockUser1, mockUser2));
        when(mockMapper.toDto(any(User.class))).thenReturn(mockDTO1, mockDTO2);

        // Act
        List<UserDto> result = service.getUsers();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(mockDTO1));
        assertTrue(result.contains(mockDTO2));
        verify(mockRepository, times(1)).get();
        verify(mockMapper, times(2)).toDto(any(User.class));
    }

    @Test
    void get_Id_WithAdmin_Should_ReturnUser() {
        // Arrange
        User admin = new User();
        admin.setAdmin(true);

        User target = new User();
        when(mockRepository.get(10)).thenReturn(target);

        // Act
        User result = service.get(10, admin);

        // Assert
        assertEquals(target, result);
        verify(mockRepository, times(1)).get(10);
    }

    @Test
    void get_Id_WithNonAdmin_Should_Throw() {
        // Arrange
        User requester = new User();
        requester.setAdmin(false);

        // Act & Assert
        assertThrows(AuthorizationException.class, () -> service.get(10, requester));
        verifyNoInteractions(mockRepository);
    }

    @Test
    void searchUsers_Should_CallRepository_And_MapResults() {
        // Arrange
        User requester = new User();
        User mockUser = new User();
        UserDto mockDTO = new UserDto();

        when(mockRepository.search("name", "email", "first", requester)).thenReturn(Collections.singletonList(mockUser));
        when(mockMapper.toDto(mockUser)).thenReturn(mockDTO);

        // Act
        List<UserDto> result = service.searchUsers("name", "email", "first", requester);

        // Assert - single mapped DTO returned
        assertEquals(1, result.size());
        assertEquals(mockDTO, result.get(0));
        verify(mockRepository, times(1)).search("name", "email", "first", requester);
        verify(mockMapper, times(1)).toDto(mockUser);
    }
}
