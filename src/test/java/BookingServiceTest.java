import com.uzinfocom.citytour.dto.BookingRequest;
import com.uzinfocom.citytour.dto.BookingResponse;
import com.uzinfocom.citytour.entity.Booking;
import com.uzinfocom.citytour.entity.Tour;
import com.uzinfocom.citytour.entity.enums.BookingStatus;
import com.uzinfocom.citytour.entity.enums.TourStatus;
import com.uzinfocom.citytour.exception.BusinessLogicException;
import com.uzinfocom.citytour.repository.BookingRepository;
import com.uzinfocom.citytour.repository.TourRepository;
import com.uzinfocom.citytour.service.TourService;
import com.uzinfocom.citytour.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingService Unit Testlari (Biznes Qoidalar)")
class BookingServiceTest {

    @Mock
    private TourRepository tourRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private TourService tourService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Tour activeTour;
    private BookingRequest validRequest;

    @BeforeEach
    void setUp() {
        activeTour = new Tour();
        activeTour.setId(1L);
        activeTour.setTitle("Samarqand Sayohati");
        activeTour.setStatus(TourStatus.PUBLISHED);
        activeTour.setStartTime(LocalDateTime.now().plusDays(2));
        activeTour.setMaxSeats(10);
        activeTour.setPricePerSeat(BigDecimal.valueOf(100000));

        validRequest = new BookingRequest();
        validRequest.setTourId(1L);
        validRequest.setCustomerName("Ali Valiyev");
        validRequest.setCustomerPhone("+998901234567");
        validRequest.setSeats(2);
    }

    // ==========================================
    // 1. BR-2: VAQT KESISHISHI / VAQT TEKSHIRUVI
    // ==========================================
    @Test
    @DisplayName("BR-2: Boshlanish vaqti o'tib ketgan turga bron qilishda BusinessLogicException otishi kerak")
    void createBooking_PastTour_ThrowsException() {
        // Turning vaqti o'tib ketgan deb belgilaymiz
        activeTour.setStartTime(LocalDateTime.now().minusDays(1));
        when(tourRepository.findById(1L)).thenReturn(Optional.of(activeTour));

        assertThrows(BusinessLogicException.class, () -> {
            bookingService.createBooking(1L, validRequest);
        });

        verify(bookingRepository, never()).save(any());
    }

    // ==========================================
    // 2. BR-3: SIG'IM (MAKSIMAL JOY) TEKSHIRUVI
    // ==========================================
    @Test
    @DisplayName("BR-3: So'ralgan joylar sig'imdan (maxSeats) oshib ketganda BusinessLogicException otishi kerak")
    void createBooking_ExceedsCapacity_ThrowsException() {
        activeTour.setMaxSeats(5);
        validRequest.setSeats(10); // Sig'imdan ko'p joy so'ralmoqda
        when(tourRepository.findById(1L)).thenReturn(Optional.of(activeTour));

        assertThrows(BusinessLogicException.class, () -> {
            bookingService.createBooking(1L, validRequest);
        });

        verify(bookingRepository, never()).save(any());
    }

    // ==========================================
    // 3. BR-6: NARX HISOBI (TOTAL PRICE)
    // ==========================================
    @Test
    @DisplayName("BR-6: Bron qilishda umumiy narx (seats * pricePerSeat) to'g'ri hisoblanishi kerak")
    void createBooking_PriceCalculation_Success() {
        when(tourRepository.findById(1L)).thenReturn(Optional.of(activeTour));

        Booking savedBooking = new Booking();
        savedBooking.setId(100L);
        savedBooking.setTour(activeTour);
        savedBooking.setSeats(2); // 2 * 100,000 = 200,000
        savedBooking.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        BookingResponse response = bookingService.createBooking(1L, validRequest);

        assertThat(response).isNotNull();
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    // ==========================================
    // 4. BR-4: DRAFT HOLATIDAGI TURGA BRON QILISH
    // ==========================================
    @Test
    @DisplayName("BR-4: DRAFT holatidagi turga bron qilish rad etilishi kerak")
    void createBooking_DraftStatus_ThrowsException() {
        activeTour.setStatus(TourStatus.DRAFT);
        when(tourRepository.findById(1L)).thenReturn(Optional.of(activeTour));

        assertThrows(BusinessLogicException.class, () -> {
            bookingService.createBooking(1L, validRequest);
        });

        verify(bookingRepository, never()).save(any());
    }

    // ==========================================
    // 5. BR-8: BRONNI BEKOR QILISH (CANCEL)
    // ==========================================
    @Test
    @DisplayName("BR-8: Bron bekor qilinganda uning statusi CANCELLED bo'lishi kerak")
    void cancelBooking_Success() {
        Long bookingId = 100L;
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setTour(activeTour);
        booking.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        bookingService.cancelBooking(bookingId);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        verify(bookingRepository, times(1)).save(booking);
    }
}