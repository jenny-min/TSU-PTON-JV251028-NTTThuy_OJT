package com.example.ojt.configs;

import com.example.ojt.entities.Genre;
import com.example.ojt.entities.Room;
import com.example.ojt.entities.User;
import com.example.ojt.repositories.GenreRepository;
import com.example.ojt.repositories.RoomRepository;
import com.example.ojt.repositories.UserRepository;
import com.example.ojt.roles.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository ur;
    private final PasswordEncoder passwordEncoder;
    private final GenreRepository genreRepository;
    private final RoomRepository roomRepository;

    @Override
    public void run(String... args) throws Exception {
        //Hash code auth
        if (ur.count() == 0) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Administrator")
                    .email("admin@gmail.com")
                    .role(Role.ADMIN)
                    .build();
            ur.save(admin);

            User user = User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user123"))
                    .fullName("User")
                    .email("user@gmail.com")
                    .role(Role.USER)
                    .build();
            ur.save(user);

            User staff = User.builder()
                    .username("staff")
                    .password(passwordEncoder.encode("staff123"))
                    .fullName("Staff")
                    .email("staff@gmail.com")
                    .role(Role.STAFF)
                    .build();
            ur.save(staff);
        }

        //Hashcode thể loại phim
        if (genreRepository.count() > 0) {
            return;
        }

        List<String> genres = List.of(
                "Hành động",
                "Phiêu lưu",
                "Hoạt hình",
                "Hài",
                "Tội phạm",
                "Tài liệu",
                "Chính kịch",
                "Gia đình",
                "Giả tưởng",
                "Lịch sử",
                "Kinh dị",
                "Âm nhạc",
                "Bí ẩn",
                "Lãng mạn",
                "Khoa học viễn tưởng",
                "Thể thao",
                "Giật gân",
                "Chiến tranh",
                "Cao bồi",
                "Võ thuật"
        );

        genres.forEach(name -> {
            Genre genre = new Genre();
            genre.setGenreName(name);
            genreRepository.save(genre);
        });

        //Hashcode phòng chiếu
        if (roomRepository.count() > 0) {
            return;
        }

        List<Room> rooms = List.of(
                createRoom(
                        "Phòng 1",
                        100,
                        10,
                        10,
                        "E5,E6,F5,F6",
                        "J5,J6",
                        true
                ),
                createRoom(
                        "Phòng 2",
                        120,
                        10,
                        12,
                        "E6,E7,F6,F7",
                        "J6,J7",
                        true
                ),
                createRoom(
                        "Phòng 3",
                        144,
                        12,
                        12,
                        "F5,F6,G5,G6",
                        "K5,K6",
                        true
                ),
                createRoom(
                        "Phòng 4",
                        80,
                        8,
                        10,
                        "D4,D5,E4,E5",
                        "H4,H5",
                        true
                ),
                createRoom(
                        "Phòng VIP",
                        64,
                        8,
                        8,
                        "C3,C4,D3,D4,E3,E4",
                        "H3,H4",
                        true
                )
        );

        roomRepository.saveAll(rooms);
    }

    //Tạo room
    private Room createRoom(
            String roomName,
            int totalSeats,
            int seatsX,
            int seatsY,
            String vipSeats,
            String coupleSeats,
            boolean status) {

        Room room = new Room();
        room.setRoomName(roomName);
        room.setTotalSeats(totalSeats);
        room.setSeatsX(seatsX);
        room.setSeatsY(seatsY);
        room.setVipSeats(vipSeats);
        room.setCoupleSeats(coupleSeats);
        room.setStatus(status);

        return room;
    }
}
