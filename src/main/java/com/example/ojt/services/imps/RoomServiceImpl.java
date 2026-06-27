package com.example.ojt.services.imps;

import com.example.ojt.entities.Room;
import com.example.ojt.repositories.RoomRepository;
import com.example.ojt.services.interfaces.RoomService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {
    private RoomRepository roomRepository;

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
}
