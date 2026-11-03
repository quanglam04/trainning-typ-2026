package com.example.coursemanagement_finalprojectmodule03;

import com.example.coursemanagement_finalprojectmodule03.model.entity.Notification;
import com.example.coursemanagement_finalprojectmodule03.model.entity.User;
import com.example.coursemanagement_finalprojectmodule03.model.repo.INotificationRepo;
import com.example.coursemanagement_finalprojectmodule03.model.repo.IUserRepo;
import com.example.coursemanagement_finalprojectmodule03.mq.EnrollmentCreatedEvent;
import com.example.coursemanagement_finalprojectmodule03.mq.EnrollmentNotificationHandler;
import com.example.coursemanagement_finalprojectmodule03.mq.ProcessedEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Week4NotificationHandlerTest {
    @Mock ProcessedEventRepository processedRepo;
    @Mock IUserRepo userRepo;
    @Mock INotificationRepo notificationRepo;
    @InjectMocks EnrollmentNotificationHandler handler;

    private final EnrollmentCreatedEvent event =
            new EnrollmentCreatedEvent("event-123", 10, 20, "Java co ban");

    @Test
    void duplicateEventDoesNotCreateAnotherNotification() {
        when(processedRepo.existsById("event-123")).thenReturn(true);
        handler.handle(event);
        verifyNoInteractions(notificationRepo, userRepo);
        verify(processedRepo, never()).saveAndFlush(any());
    }

    @Test
    void firstEventCreatesNotificationAndRecordsEventId() {
        User user = new User();
        when(userRepo.findById(10)).thenReturn(Optional.of(user));
        handler.handle(event);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepo).save(captor.capture());
        assertEquals("ENROLLMENT", captor.getValue().getType());
        assertEquals("/courses/20", captor.getValue().getTargetUrl());
        assertTrue(captor.getValue().getMessage().contains("Java co ban"));
        verify(processedRepo).saveAndFlush(argThat(e -> e.getEventId().equals("event-123")));
    }

    @Test
    void missingStudentCausesFailureSoMessageIsNotAcked() {
        when(userRepo.findById(10)).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> handler.handle(event));
        verifyNoInteractions(notificationRepo);
        verify(processedRepo, never()).saveAndFlush(any());
    }
}
