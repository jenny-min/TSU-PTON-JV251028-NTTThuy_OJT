package com.example.ojt.services.interfaces;

import com.example.ojt.entities.Room;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface RoomService {
    List<Room> getAllRooms();

    Room getRoomById(Long roomId);
}
