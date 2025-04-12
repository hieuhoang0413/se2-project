package com.se2.midterm.service;

import com.se2.midterm.entity.ContactMessage;
import com.se2.midterm.repository.ContactMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ContactMessageServiceTest {

    @Mock
    private ContactMessageRepository contactMessageRepository;

    @InjectMocks
    private ContactMessageService contactMessageService;

    private ContactMessage testMessage;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testMessage = new ContactMessage("John Doe", "john@example.com", "Hello, I need help.");
    }

    @Test
    public void testSaveMessage() {
        when(contactMessageRepository.save(any(ContactMessage.class))).thenReturn(testMessage);

        contactMessageService.saveMessage(testMessage);

        verify(contactMessageRepository).save(testMessage);  // Ensure save is called
    }

    @Test
    public void testSaveMessageWithNull() {
        doThrow(new RuntimeException("Database Error")).when(contactMessageRepository).save(any(ContactMessage.class));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            contactMessageService.saveMessage(testMessage);
        });

        assertEquals("Database Error", thrown.getMessage());
        verify(contactMessageRepository).save(testMessage);  // Ensure save is called even with exception
    }
}
