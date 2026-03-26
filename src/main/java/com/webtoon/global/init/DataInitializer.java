package com.webtoon.global.init;

import com.webtoon.domain.comic.entity.Comic;
import com.webtoon.domain.comic.repository.ComicRepository;
import com.webtoon.domain.episode.entity.Episode;
import com.webtoon.domain.episode.entity.EpisodeImage;
import com.webtoon.domain.episode.entity.EpisodeView;
import com.webtoon.domain.ranking.entity.PopularRanking;
import com.webtoon.domain.ranking.repository.PopularRankingRepository;
import com.webtoon.domain.episode.repository.EpisodeImageRepository;
import com.webtoon.domain.episode.repository.EpisodeRepository;
import com.webtoon.domain.episode.repository.EpisodeViewRepository;
import com.webtoon.domain.genre.entity.Genre;
import com.webtoon.domain.genre.repository.GenreRepository;
import com.webtoon.domain.event.entity.Event;
import com.webtoon.domain.event.repository.EventRepository;
import com.webtoon.domain.notice.entity.Notice;
import com.webtoon.domain.notice.repository.NoticeRepository;
import com.webtoon.domain.user.entity.User;
import com.webtoon.domain.user.repository.UserRepository;
import com.webtoon.global.enums.ComicStatus;
import com.webtoon.global.enums.DayOfWeek;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.webtoon.global.enums.RankingPeriod;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final ComicRepository comicRepository;
    private final EpisodeRepository episodeRepository;
    private final EpisodeImageRepository episodeImageRepository;
    private final EpisodeViewRepository episodeViewRepository;
    private final PopularRankingRepository popularRankingRepository;
    private final NoticeRepository noticeRepository;
    private final EventRepository eventRepository;
    private final PasswordEncoder passwordEncoder;

    private Map<String, Genre> genres;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("데모 데이터가 이미 존재합니다. 스킵합니다.");
            return;
        }

        log.info("데모 데이터 생성 시작");

        // ===== 유저 =====
        User admin = userRepository.save(User.builder()
                .email("admin@webtoon.com")
                .password(passwordEncoder.encode("admin1234"))
                .nickname("관리자")
                .build());
        admin.promoteToAdmin();

        User user1 = userRepository.save(User.builder()
                .email("user1@test.com")
                .password(passwordEncoder.encode("test1234"))
                .nickname("웹툰매니아")
                .build());
        user1.chargeCoin(500);

        User user2 = userRepository.save(User.builder()
                .email("user2@test.com")
                .password(passwordEncoder.encode("test1234"))
                .nickname("만화좋아")
                .build());
        user2.chargeCoin(300);

        // ===== 장르 =====
        Genre romance = genreRepository.save(new Genre("로맨스"));
        Genre action = genreRepository.save(new Genre("액션"));
        Genre fantasy = genreRepository.save(new Genre("판타지"));
        Genre daily = genreRepository.save(new Genre("일상"));
        Genre thriller = genreRepository.save(new Genre("스릴러"));
        Genre comedy = genreRepository.save(new Genre("코미디"));
        Genre drama = genreRepository.save(new Genre("드라마"));
        Genre sports = genreRepository.save(new Genre("스포츠"));

        genres = Map.of(
                "로맨스", romance, "액션", action, "판타지", fantasy, "일상", daily,
                "스릴러", thriller, "코미디", comedy, "드라마", drama, "스포츠", sports
        );

        List<Comic> allComics = new ArrayList<>();

        // ===== 만화 + 에피소드 =====
        allComics.addAll(createMondayComics());
        allComics.addAll(createTuesdayComics());
        allComics.addAll(createWednesdayComics());
        allComics.addAll(createThursdayComics());
        allComics.addAll(createFridayComics());
        allComics.addAll(createSaturdayComics());
        allComics.addAll(createSundayComics());
        allComics.addAll(createCompletedComics());

        // ===== 조회수 데모 (랭킹용) =====
        long[] viewCounts = {
                // 월요일 (14)
                52000, 21000, 185000, 320000, 67000, 28000, 41000, 95000, 12500, 7300, 3800, 155000, 63000, 230000,
                // 화요일 (12)
                42000, 8900, 15000, 3200, 78000, 110000, 54000, 6700, 19500, 33000, 145000, 87000,
                // 수요일 (13)
                280000, 38000, 1200, 92000, 56000, 23000, 170000, 8100, 44000, 130000, 5500, 31000, 68000,
                // 목요일 (13)
                124000, 72000, 98000, 5600, 83000, 14000, 47000, 210000, 36000, 2800, 61000, 115000, 9200,
                // 금요일 (13)
                145000, 7800, 162000, 29000, 88000, 4100, 53000, 190000, 71000, 16000, 37000, 103000, 500,
                // 토요일 (12)
                35000, 210000, 4500, 132000, 58000, 22000, 85000, 11000, 168000, 43000, 7600, 500000,
                // 일요일 (12)
                175000, 12000, 88000, 245000, 31000, 9800, 66000, 140000, 24000, 51000, 3100, 106000,
                // 완결 (16)
                203000, 67000, 156000, 410000, 290000, 130000, 82000, 350000, 197000, 45000, 270000, 18000, 112000, 160000, 74000, 225000
        };
        for (int i = 0; i < allComics.size() && i < viewCounts.length; i++) {
            allComics.get(i).updateViewCount(viewCounts[i]);
        }

        // ===== 인기 랭킹 =====
        createRankings(allComics);

        // ===== 공지사항 =====
        createNotices();

        // ===== 이벤트 =====
        createEvents();

        log.info("데모 데이터 생성 완료: 유저 3명, 장르 8개, 만화 {}개, 공지사항 10개, 이벤트 5개", allComics.size());
    }

    // ────────── 월요일 웹툰 (14) ──────────
    private List<Comic> createMondayComics() {
        List<Comic> comics = new ArrayList<>();

        comics.add(buildComic("여신강림", "야옹이",
                "외모 콤플렉스를 가진 소녀가 메이크업으로 변신하며 벌어지는 이야기",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.MON, DayOfWeek.TUE), Set.of("로맨스", "드라마"),
                10, 3, "9.2", "comic1"));

        comics.add(buildComic("슬램덩크 리메이크", "이노우에 타케히코",
                "농구에 빠진 소년들의 뜨거운 이야기",
                ComicStatus.ONGOING, 2, Set.of(DayOfWeek.MON, DayOfWeek.WED, DayOfWeek.FRI), Set.of("스포츠", "드라마"),
                8, 3, "8.5", "comic6"));

        comics.add(buildComic("화산귀환", "비가",
                "화산파의 매화검존이 100년 후 환생하여 다시 무림을 평정하는 이야기",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.MON), Set.of("액션", "드라마"),
                25, 3, "9.5", "comic11"));

        comics.add(buildComic("마음의 소리", "조석",
                "작가 조석의 일상을 과장되게 그린 개그 웹툰",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.MON), Set.of("코미디", "일상"),
                30, 2, "8.8", "comic12"));

        comics.add(buildComic("어느 날 공주가 되어버렸다", "플루토스 / 스푼",
                "소설 속 악역 공주로 빙의한 여자의 생존기",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.MON, DayOfWeek.THU), Set.of("판타지", "로맨스"),
                22, 3, "9.0", "comic13"));

        comics.add(buildComic("외모지상주의", "박태준",
                "못생긴 소년이 잘생긴 몸을 얻게 되면서 벌어지는 학원 이야기",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.MON), Set.of("액션", "드라마"),
                28, 2, "8.3", "comic31"));

        comics.add(buildComic("윈드브레이커", "조용석",
                "자전거 크루들의 속도와 우정 이야기",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.MON), Set.of("스포츠", "액션"),
                20, 3, "8.7", "comic32"));

        comics.add(buildComic("독립일기", "자까",
                "혼자 독립해서 살아가는 청춘의 일상 에세이",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.MON), Set.of("일상", "코미디"),
                15, 2, "7.8", "comic33"));

        comics.add(buildComic("나노마신", "한중월야 / 금강불괴",
                "나노마신을 주입받은 마교 서자의 무림 정복기",
                ComicStatus.ONGOING, 1, Set.of(DayOfWeek.MON, DayOfWeek.FRI), Set.of("판타지", "액션"),
                18, 5, "9.1", "comic34"));

        comics.add(buildComic("청춘블라썸", "네온비",
                "시골에서 올라온 소녀의 서울 적응기와 풋풋한 사랑",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.MON), Set.of("로맨스", "일상"),
                12, 3, "7.5", "comic35"));

        comics.add(buildComic("무사만리행", "이재학",
                "조선시대 무사의 실크로드 여행기",
                ComicStatus.ONGOING, 1, Set.of(DayOfWeek.MON), Set.of("액션", "드라마"),
                8, 5, "8.0", "comic36"));

        comics.add(buildComic("입학용병", "YC / 콘조",
                "용병 출신 전학생이 학교에 적응하는 이야기",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.MON, DayOfWeek.SAT), Set.of("액션", "코미디"),
                24, 3, "8.9", "comic37"));

        comics.add(buildComic("연의 편지", "한산",
                "조선시대를 배경으로 한 애틋한 편지 로맨스",
                ComicStatus.ONGOING, 2, Set.of(DayOfWeek.MON), Set.of("로맨스", "드라마"),
                10, 3, "8.1", "comic38"));

        comics.add(buildComic("전생검신", "도근 / 임기영",
                "검의 신으로 불리던 자가 500년 뒤 환생하는 무협 이야기",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.MON, DayOfWeek.WED), Set.of("판타지", "액션"),
                30, 3, "9.3", "comic39"));

        return comics;
    }

    // ────────── 화요일 웹툰 (12) ──────────
    private List<Comic> createTuesdayComics() {
        List<Comic> comics = new ArrayList<>();

        comics.add(buildComic("재혼 황후", "Alpha Tart / 숨",
                "황후가 폐위된 후 다른 나라의 황후가 되는 이야기",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.TUE, DayOfWeek.SAT), Set.of("판타지", "로맨스"),
                10, 3, "8.9", "comic9"));

        comics.add(buildComic("살인자ㅇ난감", "꿀딴지곰",
                "연쇄살인마와 특수능력을 가진 형사의 두뇌 싸움",
                ComicStatus.ONGOING, 1, Set.of(DayOfWeek.TUE), Set.of("스릴러", "액션"),
                18, 5, null, "comic14"));

        comics.add(buildComic("나빌레라", "HUN / 지민",
                "70세 할아버지가 발레에 도전하는 감동 스토리",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.TUE, DayOfWeek.FRI), Set.of("드라마", "일상"),
                12, 2, "9.6", "comic15"));

        comics.add(buildComic("용사가 돌아왔다", "낭만도사",
                "마왕을 물리치고 돌아온 용사의 좌충우돌 현대 적응기",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.TUE), Set.of("코미디", "판타지"),
                15, 3, null, "comic16"));

        comics.add(buildComic("소녀의 세계", "모랑지",
                "평범한 여고생의 학교 생활과 우정 이야기",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.TUE), Set.of("일상", "드라마"),
                20, 2, "8.6", "comic40"));

        comics.add(buildComic("이번 생은 가주가 되겠습니다", "민서린 / 스토",
                "회귀한 여주가 가문을 이끌어가는 판타지 로맨스",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.TUE), Set.of("판타지", "로맨스"),
                16, 3, "9.0", "comic41"));

        comics.add(buildComic("퀘스트 지상주의", "타일 / 범진",
                "게임 시스템이 현실에 구현된 세상에서 살아남기",
                ComicStatus.ONGOING, 1, Set.of(DayOfWeek.TUE, DayOfWeek.THU), Set.of("판타지", "액션"),
                22, 5, "8.4", "comic42"));

        comics.add(buildComic("헬퍼", "색소",
                "갱단의 잔혹한 세계에서 빛나는 인간의 의지",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.TUE), Set.of("액션", "드라마"),
                10, 3, "9.2", "comic43"));

        comics.add(buildComic("로또황녀", "아리스 / 하린",
                "빈민가 출신이 황녀로 각성하며 벌어지는 궁중 이야기",
                ComicStatus.ONGOING, 2, Set.of(DayOfWeek.TUE), Set.of("로맨스", "판타지"),
                14, 3, "7.6", "comic44"));

        comics.add(buildComic("운동천재 홍대광", "은성 / 이학",
                "체육만 올올A인 소년의 스포츠 도전기",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.TUE), Set.of("스포츠", "코미디"),
                11, 2, "7.9", "comic45"));

        comics.add(buildComic("무한전생", "리빙스턴 / 그란비",
                "죽을 때마다 다른 세계로 전생하는 용사의 무한 루프",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.TUE, DayOfWeek.SUN), Set.of("판타지", "액션"),
                26, 3, "8.8", "comic46"));

        comics.add(buildComic("찐사랑 리턴즈", "주찬",
                "30대 커플의 현실 연애 에피소드",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.TUE), Set.of("로맨스", "코미디"),
                18, 2, "8.2", "comic47"));

        return comics;
    }

    // ────────── 수요일 웹툰 (13) ──────────
    private List<Comic> createWednesdayComics() {
        List<Comic> comics = new ArrayList<>();

        comics.add(buildComic("나 혼자만 레벨업", "추공 / DUBU",
                "세계 최약체 헌터가 각성하여 최강이 되는 이야기",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.WED), Set.of("액션", "판타지"),
                15, 3, "9.7", "comic2"));

        comics.add(buildComic("연애혁명", "232",
                "평범한 고등학생들의 설레는 학원 로맨스",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.WED), Set.of("드라마", "로맨스"),
                20, 2, "8.3", "comic17"));

        comics.add(buildComic("그림자의 집", "유현",
                "낡은 아파트에 숨겨진 소름끼치는 비밀",
                ComicStatus.ONGOING, 1, Set.of(DayOfWeek.WED, DayOfWeek.SUN), Set.of("스릴러", "드라마"),
                9, 5, null, "comic18"));

        comics.add(buildComic("갓 오브 하이스쿨", "박용제",
                "전국 고등학교 최강자를 가리는 격투 대회",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.WED), Set.of("액션", "판타지"),
                25, 3, "8.8", "comic48"));

        comics.add(buildComic("기기괴괴", "오성대",
                "한국형 괴담과 도시전설을 옴니버스로 풀어낸 공포물",
                ComicStatus.ONGOING, 1, Set.of(DayOfWeek.WED), Set.of("스릴러", "드라마"),
                14, 5, "8.0", "comic49"));

        comics.add(buildComic("하루만 네가 되고 싶어", "오혜린",
                "서로의 몸이 바뀐 두 남녀의 로맨틱 코미디",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.WED), Set.of("로맨스", "코미디"),
                12, 2, "7.7", "comic50"));

        comics.add(buildComic("헌터X로드", "만수장 / 김종관",
                "몬스터가 출현한 세계에서 헌터로 살아가는 남자의 이야기",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.WED), Set.of("판타지", "액션"),
                22, 3, "9.1", "comic51"));

        comics.add(buildComic("고양이 아파트", "릴랑",
                "고양이들이 사는 아파트의 아기자기한 일상",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.WED), Set.of("일상", "코미디"),
                8, 2, "7.3", "comic52"));

        comics.add(buildComic("데스게임", "정주영",
                "폐교에 갇힌 학생들의 목숨을 건 생존 게임",
                ComicStatus.ONGOING, 1, Set.of(DayOfWeek.WED, DayOfWeek.SAT), Set.of("스릴러", "액션"),
                16, 5, "8.5", "comic53"));

        comics.add(buildComic("사내맞선", "해하 / 김하자",
                "회장님의 손녀와 무심한 엘리트의 계약 로맨스",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.WED), Set.of("로맨스", "드라마"),
                20, 3, "9.2", "comic54"));

        comics.add(buildComic("오늘도 귀여워", "시니 / 해피",
                "반려동물과 함께하는 집사의 일상 기록",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.WED), Set.of("일상", "코미디"),
                10, 2, "6.8", "comic55"));

        comics.add(buildComic("축구왕 레오", "박진수",
                "무명 축구선수가 월드클래스로 성장하는 이야기",
                ComicStatus.ONGOING, 2, Set.of(DayOfWeek.WED, DayOfWeek.THU), Set.of("스포츠", "드라마"),
                15, 3, "8.1", "comic56"));

        comics.add(buildComic("회귀한 천재 플레이어", "도혁 / 미래",
                "프로게이머가 10년 전으로 회귀하여 다시 도전하는 이야기",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.WED), Set.of("판타지", "드라마"),
                18, 3, "8.6", "comic57"));

        return comics;
    }

    // ────────── 목요일 웹툰 (13) ──────────
    private List<Comic> createThursdayComics() {
        List<Comic> comics = new ArrayList<>();

        comics.add(buildComic("유미의 세포들", "이동건",
                "평범한 직장인 유미의 머릿속 세포들이 펼치는 이야기",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.THU, DayOfWeek.FRI), Set.of("일상", "코미디"),
                12, 3, "9.1", "comic3"));

        comics.add(buildComic("템빨", "박태준 / 콘조",
                "아이템으로 세상을 뒤집는 먼치킨 게임 판타지",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.THU), Set.of("판타지", "액션"),
                25, 3, "8.1", "comic19"));

        comics.add(buildComic("내 남편과 결혼해줘", "성소작 / LICO",
                "배신한 남편과 친구에게 복수하기 위한 회귀 로맨스",
                ComicStatus.ONGOING, 1, Set.of(DayOfWeek.THU), Set.of("로맨스", "코미디"),
                20, 5, "9.3", "comic20"));

        comics.add(buildComic("고교격투기", "이현세",
                "약골 고등학생이 격투기 천재로 성장하는 이야기",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.THU, DayOfWeek.SUN), Set.of("스포츠", "코미디"),
                14, 2, null, "comic21"));

        comics.add(buildComic("마왕의 아이를 키우게 되었다", "에나 / 루카",
                "영웅이 마왕의 아기를 맡게 되며 벌어지는 육아 판타지",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.THU), Set.of("판타지", "코미디"),
                16, 3, "8.4", "comic58"));

        comics.add(buildComic("언더더스킨", "킴제르",
                "피부 밑에 숨겨진 비밀을 파헤치는 의학 스릴러",
                ComicStatus.ONGOING, 1, Set.of(DayOfWeek.THU), Set.of("스릴러", "드라마"),
                10, 5, "7.9", "comic59"));

        comics.add(buildComic("댄스댄스", "이하나",
                "방송댄스 동아리 고등학생들의 성장기",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.THU), Set.of("일상", "드라마"),
                13, 2, "7.5", "comic60"));

        comics.add(buildComic("만렙귀환", "제스 / 오란",
                "이세계에서 만렙 찍고 돌아온 남자의 현실 적응기",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.THU), Set.of("판타지", "액션"),
                30, 3, "9.2", "comic61"));

        comics.add(buildComic("하얀 설탕", "윤이나",
                "제과제빵사를 꿈꾸는 소녀의 성장 이야기",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.THU), Set.of("일상", "로맨스"),
                9, 2, "7.1", "comic62"));

        comics.add(buildComic("녹의 전설", "이건 / 박인",
                "사슴으로 변하는 저주를 받은 왕자의 모험",
                ComicStatus.ONGOING, 1, Set.of(DayOfWeek.THU), Set.of("판타지", "드라마"),
                7, 5, "6.5", "comic63"));

        comics.add(buildComic("프리드로우", "전선욱",
                "농구를 통해 성장하는 소년들의 열정과 우정",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.THU, DayOfWeek.SUN), Set.of("스포츠", "드라마"),
                22, 3, "8.7", "comic64"));

        comics.add(buildComic("악역의 엔딩은 죽음뿐", "권겨울 / 해민",
                "게임 속 악역으로 빙의한 주인공의 생존 전략",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.THU), Set.of("판타지", "로맨스"),
                19, 3, "9.0", "comic65"));

        comics.add(buildComic("부활남", "채정택",
                "죽어도 다시 살아나는 남자의 미스터리",
                ComicStatus.ONGOING, 1, Set.of(DayOfWeek.THU), Set.of("스릴러", "액션"),
                11, 5, "7.8", "comic66"));

        return comics;
    }

    // ────────── 금요일 웹툰 (13) ──────────
    private List<Comic> createFridayComics() {
        List<Comic> comics = new ArrayList<>();

        comics.add(buildComic("약한영웅", "서패스 / 김진석",
                "교실 안의 폭력에 맞서 싸우는 약한 소년의 전략",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.FRI), Set.of("액션", "스릴러"),
                25, 3, "9.4", "comic22"));

        comics.add(buildComic("와이프 말을 들어라", "윤태호",
                "결혼 10년차 부부의 좌충우돌 일상 이야기",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.FRI), Set.of("일상", "드라마"),
                16, 2, null, "comic23"));

        comics.add(buildComic("선배 이 색은 어때요", "안이슬",
                "미대생 선후배의 색채 가득한 캠퍼스 로맨스",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.FRI), Set.of("로맨스", "일상"),
                14, 3, "8.3", "comic67"));

        comics.add(buildComic("사신소년", "류재이",
                "사신의 눈을 가진 소년이 죽음의 비밀을 풀어가는 이야기",
                ComicStatus.ONGOING, 1, Set.of(DayOfWeek.FRI), Set.of("스릴러", "판타지"),
                10, 5, "7.6", "comic68"));

        comics.add(buildComic("광마회귀", "동키 / 한승우",
                "마도의 광마가 과거로 돌아가 정도를 걷는 무협",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.FRI), Set.of("액션", "판타지"),
                20, 3, "9.1", "comic69"));

        comics.add(buildComic("집이 없어", "와난",
                "전세 사기를 당한 청년의 좌충우돌 생존기",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.FRI), Set.of("일상", "코미디"),
                8, 2, "6.9", "comic70"));

        comics.add(buildComic("튜토리얼 탑의 고급 플레이어", "고기 / 최유동",
                "탑 공략 시스템 속 전략적 생존 게임",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.FRI), Set.of("판타지", "액션"),
                22, 3, "8.9", "comic71"));

        comics.add(buildComic("피라미드 게임", "달밤",
                "학교 내 계급 시스템에 반기를 드는 전학생의 이야기",
                ComicStatus.ONGOING, 1, Set.of(DayOfWeek.FRI), Set.of("드라마", "스릴러"),
                18, 5, "9.2", "comic72"));

        comics.add(buildComic("킹스맨 하이", "방구석 프로듀서",
                "잉글랜드 유스 리그에 도전하는 한국 소년 축구 이야기",
                ComicStatus.ONGOING, 2, Set.of(DayOfWeek.FRI), Set.of("스포츠", "드라마"),
                15, 3, "8.0", "comic73"));

        comics.add(buildComic("심야식당 서울편", "미나",
                "서울 골목 심야식당의 따뜻한 음식과 사람 이야기",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.FRI), Set.of("일상", "드라마"),
                11, 2, "7.4", "comic74"));

        comics.add(buildComic("레벨업만 하는 남자", "지준 / 강효",
                "레벨업 시스템을 얻은 평범한 직장인의 성장기",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.FRI), Set.of("판타지", "액션"),
                17, 3, "8.5", "comic75"));

        comics.add(buildComic("간 떨어지는 동거", "이봄 / 해릿",
                "구미호와 대학생의 동거 로맨스 코미디",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.FRI), Set.of("로맨스", "코미디"),
                20, 2, "8.8", "comic76"));

        comics.add(buildComic("몽마르트의 별", "초아",
                "파리 유학생 화가의 꿈과 사랑 이야기",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.FRI), Set.of("로맨스", "드라마"),
                5, 3, "6.0", "comic77"));

        return comics;
    }

    // ────────── 토요일 웹툰 (12) ──────────
    private List<Comic> createSaturdayComics() {
        List<Comic> comics = new ArrayList<>();

        comics.add(buildComic("스위트홈", "김칸비 / 황영찬",
                "괴물이 된 사람들과 살아남기 위한 사투",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.SAT), Set.of("스릴러", "드라마"),
                8, 3, "9.0", "comic4"));

        comics.add(buildComic("신의 탑", "SIU",
                "탑 꼭대기에 모든 것이 있다는 전설, 소년은 탑에 올라간다",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.SAT), Set.of("판타지", "드라마"),
                30, 3, "8.7", "comic24"));

        comics.add(buildComic("하렘의 남자들", "인영 / 세리",
                "황녀로 빙의한 여주인공의 후궁 경영 로맨스 판타지",
                ComicStatus.ONGOING, 1, Set.of(DayOfWeek.SAT), Set.of("로맨스", "판타지"),
                11, 5, null, "comic25"));

        comics.add(buildComic("언터처블", "마사토끼",
                "은퇴한 범죄 조직 보스의 아들이 학교에 전학오면서 벌어지는 이야기",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.SAT), Set.of("액션", "드라마"),
                22, 3, "8.6", "comic78"));

        comics.add(buildComic("소꿉친구가 너무 예뻐", "아루니",
                "어린 시절 소꿉친구와의 재회 로맨스",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.SAT), Set.of("로맨스", "일상"),
                14, 2, "7.8", "comic79"));

        comics.add(buildComic("좀비가 된 나", "호린",
                "좀비 바이러스에 감염되었지만 의식이 남아있는 남자의 서바이벌",
                ComicStatus.ONGOING, 1, Set.of(DayOfWeek.SAT), Set.of("스릴러", "액션"),
                10, 5, "8.2", "comic80"));

        comics.add(buildComic("배드보이메모리즈", "강민수",
                "복싱에 인생을 건 불량 소년의 링 위 성장기",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.SAT), Set.of("스포츠", "드라마"),
                18, 3, "8.3", "comic81"));

        comics.add(buildComic("나 혼자 네크로맨서", "일라이트 / 고니",
                "유일한 네크로맨서 직업을 가진 헌터의 이야기",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.SAT), Set.of("판타지", "액션"),
                12, 3, "7.5", "comic82"));

        comics.add(buildComic("황제의 외동딸", "율이매 / 핀",
                "대공가의 외동딸이 황제의 딸로 입양되는 판타지",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.SAT), Set.of("판타지", "로맨스"),
                26, 3, "9.3", "comic83"));

        comics.add(buildComic("먹는 존재", "하늘과자",
                "먹방 유튜버의 일상과 음식 이야기",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.SAT), Set.of("일상", "코미디"),
                15, 2, "7.0", "comic84"));

        comics.add(buildComic("우리집 고양이", "춘삼 / 마루",
                "다섯 마리 고양이와 집사의 유쾌한 동거 에세이",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.SAT), Set.of("일상", "코미디"),
                7, 2, "6.3", "comic85"));

        comics.add(buildComic("마법천자문", "시리얼 / 홍승우",
                "한자의 마법으로 모험을 떠나는 소년의 판타지",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.SAT), Set.of("판타지", "액션"),
                28, 3, "9.5", "comic86"));

        return comics;
    }

    // ────────── 일요일 웹툰 (12) ──────────
    private List<Comic> createSundayComics() {
        List<Comic> comics = new ArrayList<>();

        comics.add(buildComic("전지적 독자 시점", "싱숑 / 슬리피-C",
                "소설 속 세계가 현실이 된다면",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.SUN), Set.of("판타지", "액션"),
                10, 3, "9.8", "comic5"));

        comics.add(buildComic("호랑이형님", "임인스",
                "호랑이가 인간 세상에서 살아남는 좌충우돌 코미디",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.SUN), Set.of("일상", "코미디"),
                19, 2, "7.9", "comic26"));

        comics.add(buildComic("비질란테", "김규삼",
                "낮에는 경찰대생, 밤에는 사적 제재를 가하는 남자",
                ComicStatus.ONGOING, 1, Set.of(DayOfWeek.SUN), Set.of("드라마", "스릴러"),
                24, 5, "8.6", "comic27"));

        comics.add(buildComic("나이트런", "김성민",
                "우주를 배경으로 한 대규모 SF 판타지 전쟁",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.SUN), Set.of("판타지", "액션"),
                28, 3, "9.4", "comic87"));

        comics.add(buildComic("오렌지마말레이드", "석우",
                "뱀파이어 소녀의 비밀스러운 학원 로맨스",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.SUN), Set.of("로맨스", "판타지"),
                15, 2, "8.0", "comic88"));

        comics.add(buildComic("체크포인트", "배민규",
                "세이브 포인트 능력을 가진 남자의 위험한 일상",
                ComicStatus.ONGOING, 1, Set.of(DayOfWeek.SUN), Set.of("스릴러", "판타지"),
                10, 5, "7.7", "comic89"));

        comics.add(buildComic("복학왕", "기안84",
                "복학생의 좌충우돌 대학생활 이야기",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.SUN), Set.of("코미디", "일상"),
                20, 2, "8.5", "comic90"));

        comics.add(buildComic("백수세끼", "유장한",
                "백수 삼형제의 하루 세끼 해결 프로젝트",
                ComicStatus.ONGOING, 5, Set.of(DayOfWeek.SUN), Set.of("일상", "코미디"),
                18, 2, "8.9", "comic91"));

        comics.add(buildComic("궁극의 힐러", "도수 / 나래",
                "힐러 전직으로 최강이 되어가는 판타지 모험",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.SUN), Set.of("판타지", "액션"),
                14, 3, "7.3", "comic92"));

        comics.add(buildComic("타격왕", "이준호",
                "야구부 에이스가 타자로 전향하는 스포츠 드라마",
                ComicStatus.ONGOING, 2, Set.of(DayOfWeek.SUN), Set.of("스포츠", "드라마"),
                16, 3, "8.4", "comic93"));

        comics.add(buildComic("인생존망", "창천향로",
                "강호 무림의 패권을 두고 펼쳐지는 무협 대서사시",
                ComicStatus.ONGOING, 1, Set.of(DayOfWeek.SUN), Set.of("액션", "드라마"),
                6, 5, "6.2", "comic94"));

        comics.add(buildComic("소년시절의 너", "미우 / 그녀",
                "고등학교 시절 첫사랑의 기억을 따라가는 성장 로맨스",
                ComicStatus.ONGOING, 3, Set.of(DayOfWeek.SUN), Set.of("로맨스", "드라마"),
                20, 3, "8.8", "comic95"));

        return comics;
    }

    // ────────── 완결 웹툰 (16) ──────────
    private List<Comic> createCompletedComics() {
        List<Comic> comics = new ArrayList<>();

        comics.add(buildComic("치즈인더트랩", "순끼",
                "완벽남 유정과 평범녀 홍설의 미스터리 로맨스",
                ComicStatus.COMPLETED, 5, Set.of(), Set.of("로맨스", "일상"),
                20, 3, "9.1", "comic7"));

        comics.add(buildComic("바스타드", "카르나지 / 황영찬",
                "연쇄살인마 아버지를 가진 아들의 이야기",
                ComicStatus.COMPLETED, 3, Set.of(), Set.of("액션", "스릴러"),
                15, 3, "9.3", "comic8"));

        comics.add(buildComic("목욕의 신", "하일권",
                "동네 목욕탕에서 벌어지는 유쾌한 이야기",
                ComicStatus.COMPLETED, 5, Set.of(), Set.of("코미디", "일상"),
                25, 2, "8.4", "comic10"));

        comics.add(buildComic("노블레스", "손제호 / 이광수",
                "820년간 잠들어 있던 귀족이 현대에 깨어나는 이야기",
                ComicStatus.COMPLETED, 5, Set.of(), Set.of("판타지", "액션"),
                30, 2, "8.9", "comic28"));

        comics.add(buildComic("미생", "윤태호",
                "비정규직 장그래의 치열한 회사 생존기",
                ComicStatus.COMPLETED, 3, Set.of(), Set.of("드라마", "로맨스"),
                25, 3, "9.6", "comic29"));

        comics.add(buildComic("타인은 지옥이다", "김용키",
                "서울 고시원에서 벌어지는 섬뜩한 심리 스릴러",
                ComicStatus.COMPLETED, 1, Set.of(), Set.of("스릴러", "판타지"),
                20, 5, "9.0", "comic30"));

        comics.add(buildComic("트레이스", "고영훈",
                "초능력자들의 숨겨진 존재와 인간의 갈등",
                ComicStatus.COMPLETED, 3, Set.of(), Set.of("액션", "판타지"),
                22, 3, "8.2", "comic96"));

        comics.add(buildComic("이웃사람", "강풀",
                "평범한 이웃에게 숨겨진 소름끼치는 비밀",
                ComicStatus.COMPLETED, 5, Set.of(), Set.of("스릴러", "드라마"),
                18, 2, "9.4", "comic97"));

        comics.add(buildComic("열렙전사", "박성규",
                "VR 게임 속 최하급 전사의 역전 스토리",
                ComicStatus.COMPLETED, 3, Set.of(), Set.of("판타지", "코미디"),
                28, 3, "8.7", "comic98"));

        comics.add(buildComic("오즈랜드", "솔비",
                "마법의 나라 오즈를 배경으로 한 여행기",
                ComicStatus.COMPLETED, 5, Set.of(), Set.of("판타지", "일상"),
                12, 2, "7.0", "comic99"));

        comics.add(buildComic("이태원 클라쓰", "광진",
                "아버지의 복수를 위해 요식업계에 뛰어든 청년의 성공기",
                ComicStatus.COMPLETED, 3, Set.of(), Set.of("드라마", "액션"),
                25, 3, "9.5", "comic100"));

        comics.add(buildComic("패밀리맨", "이두한",
                "평범한 가장이 비밀 조직에 얽히게 되는 이야기",
                ComicStatus.COMPLETED, 1, Set.of(), Set.of("스릴러", "드라마"),
                10, 5, "7.6", "comic101"));

        comics.add(buildComic("하이브", "김규삼",
                "거대 곤충이 세상을 뒤덮은 포스트아포칼립스 생존물",
                ComicStatus.COMPLETED, 3, Set.of(), Set.of("스릴러", "액션"),
                20, 3, "8.8", "comic102"));

        comics.add(buildComic("대학일기", "자까",
                "대학생활의 소소한 일상과 고민을 담은 에세이",
                ComicStatus.COMPLETED, 5, Set.of(), Set.of("일상", "코미디"),
                22, 2, "8.5", "comic103"));

        comics.add(buildComic("테니스의 왕자", "채울 / 킴",
                "천재 테니스 소년의 전국 대회 도전기",
                ComicStatus.COMPLETED, 3, Set.of(), Set.of("스포츠", "드라마"),
                18, 3, "7.9", "comic104"));

        comics.add(buildComic("덴마", "양영순",
                "우주를 배경으로 한 택배기사의 거대한 음모 이야기",
                ComicStatus.COMPLETED, 3, Set.of(), Set.of("판타지", "액션"),
                30, 3, "9.2", "comic105"));

        return comics;
    }

    // ────────── 공지사항 (10) ──────────
    private void createRankings(List<Comic> allComics) {
        List<Comic> sorted = new ArrayList<>(allComics);
        sorted.sort(Comparator.comparingLong(Comic::getViewCount).reversed());

        int rank = 1;
        for (int i = 0; i < Math.min(sorted.size(), 30); i++) {
            Comic c = sorted.get(i);
            // 일간 랭킹
            popularRankingRepository.save(PopularRanking.builder()
                    .comic(c).rank(rank).viewCount(c.getViewCount())
                    .period(RankingPeriod.DAILY).build());
            // 주간 랭킹 (조회수 x 3 정도로)
            popularRankingRepository.save(PopularRanking.builder()
                    .comic(c).rank(rank).viewCount(c.getViewCount() * 3)
                    .period(RankingPeriod.WEEKLY).build());
            rank++;
        }
    }

    private void createNotices() {
        noticeRepository.save(Notice.builder()
                .title("KYUNGWON TOON 서비스 오픈 안내")
                .content("안녕하세요, KYUNGWON TOON입니다.\n\n오랜 준비 끝에 KYUNGWON TOON 서비스를 정식 오픈하게 되었습니다!\n다양한 장르의 웹툰을 매일 무료로 즐기실 수 있으며, 유료 에피소드는 코인으로 구매하실 수 있습니다.\n\n많은 관심과 이용 부탁드립니다.\n감사합니다.")
                .isImportant(true)
                .build());

        noticeRepository.save(Notice.builder()
                .title("코인 충전 이벤트 안내")
                .content("KYUNGWON TOON 오픈 기념 코인 충전 이벤트를 진행합니다!\n\n이벤트 기간: 2026.03.20 ~ 2026.04.20\n이벤트 내용: 코인 충전 시 50% 보너스 코인 추가 지급\n\n예시)\n- 100코인 충전 시 → 150코인 지급\n- 500코인 충전 시 → 750코인 지급\n\n이번 기회를 놓치지 마세요!")
                .isImportant(true)
                .build());

        noticeRepository.save(Notice.builder()
                .title("서비스 이용약관 개정 안내")
                .content("안녕하세요, KYUNGWON TOON입니다.\n\n서비스 이용약관이 아래와 같이 개정됩니다.\n\n개정일: 2026.04.01\n주요 변경 사항:\n1. 개인정보 처리방침 업데이트\n2. 유료 콘텐츠 환불 정책 명확화\n3. 이용자 권리 및 의무 조항 보완\n\n자세한 내용은 이용약관 페이지에서 확인하실 수 있습니다.")
                .isImportant(false)
                .build());

        noticeRepository.save(Notice.builder()
                .title("신규 웹툰 업데이트 안내")
                .content("이번 주 신규 웹툰이 업데이트되었습니다!\n\n- [월요일] 전생검신 - 검의 신으로 불리던 자가 500년 뒤 환생하는 무협 이야기\n- [화요일] 무한전생 - 죽을 때마다 다른 세계로 전생하는 용사의 무한 루프\n- [수요일] 회귀한 천재 플레이어 - 프로게이머가 10년 전으로 회귀\n\n많은 관심 부탁드립니다!")
                .isImportant(false)
                .build());

        noticeRepository.save(Notice.builder()
                .title("시스템 점검 안내")
                .content("안녕하세요, KYUNGWON TOON입니다.\n\n보다 안정적인 서비스 제공을 위해 시스템 점검을 실시합니다.\n\n점검 일시: 2026.03.25 (수) 02:00 ~ 06:00 (4시간)\n점검 내용: 서버 인프라 업그레이드 및 보안 패치\n\n점검 시간 동안 서비스 이용이 제한됩니다.\n이용에 불편을 드려 죄송합니다.")
                .isImportant(false)
                .build());

        noticeRepository.save(Notice.builder()
                .title("앱 업데이트 안내 (v1.1.0)")
                .content("KYUNGWON TOON 앱이 v1.1.0으로 업데이트되었습니다.\n\n주요 변경 사항:\n1. 다크 모드 지원\n2. 웹툰 다운로드 기능 추가\n3. 알림 설정 기능 개선\n4. 일부 버그 수정\n\n최신 버전으로 업데이트하여 더욱 편리한 서비스를 이용해 보세요!")
                .isImportant(false)
                .build());

        noticeRepository.save(Notice.builder()
                .title("작가 팬미팅 이벤트 안내")
                .content("KYUNGWON TOON 인기 작가와의 팬미팅 이벤트를 개최합니다!\n\n일시: 2026.04.15 (수) 14:00\n장소: 서울 강남구 코엑스 컨퍼런스홀\n참여 작가: 비가(화산귀환), 추공(나 혼자만 레벨업), 이동건(유미의 세포들)\n\n참가 신청: 앱 내 이벤트 페이지에서 응모\n응모 기간: 2026.03.25 ~ 2026.04.10\n\n당첨자 발표: 2026.04.12")
                .isImportant(false)
                .build());

        noticeRepository.save(Notice.builder()
                .title("주간 인기 웹툰 TOP 10 발표")
                .content("이번 주 가장 많은 사랑을 받은 웹툰 TOP 10을 발표합니다!\n\n1위. 마법천자문\n2위. 나 혼자만 레벨업\n3위. 화산귀환\n4위. 마음의 소리\n5위. 전지적 독자 시점\n6위. 나이트런\n7위. 전생검신\n8위. 여신강림\n9위. 약한영웅\n10위. 황제의 외동딸\n\n다음 주에도 많은 응원 부탁드립니다!")
                .isImportant(false)
                .build());

        noticeRepository.save(Notice.builder()
                .title("개인정보 보호 강화 안내")
                .content("안녕하세요, KYUNGWON TOON입니다.\n\n회원님의 소중한 개인정보를 더욱 안전하게 보호하기 위해 보안 정책을 강화합니다.\n\n변경 사항:\n1. 2단계 인증(2FA) 도입\n2. 비밀번호 정책 강화 (8자리 이상, 특수문자 포함)\n3. 로그인 이력 확인 기능 추가\n4. 개인정보 자동 파기 주기 단축\n\n적용일: 2026.04.01\n안전한 서비스 이용을 위해 협조 부탁드립니다.")
                .isImportant(false)
                .build());

        noticeRepository.save(Notice.builder()
                .title("고객센터 운영시간 변경 안내")
                .content("안녕하세요, KYUNGWON TOON입니다.\n\n고객센터 운영시간이 아래와 같이 변경됩니다.\n\n변경 전: 평일 09:00 ~ 18:00\n변경 후: 평일 09:00 ~ 21:00, 토요일 10:00 ~ 17:00\n\n적용일: 2026.04.01\n\n더욱 편리하게 문의하실 수 있도록 운영시간을 확대하였습니다.\n일요일 및 공휴일은 휴무입니다.")
                .isImportant(false)
                .build());
    }

    // ────────── 이벤트 ──────────
    private void createEvents() {
        LocalDate startDate = LocalDate.of(2026, 3, 1);
        LocalDate endDate = LocalDate.of(2026, 12, 31);

        eventRepository.save(Event.builder()
                .title("KYUNGWON TOON 그랜드 오픈!")
                .description("KYUNGWON TOON이 정식 오픈했습니다! 다양한 장르의 웹툰을 매일 무료로 즐겨보세요.")
                .bgColor("#1a3a2a")
                .linkUrl("/")
                .startDate(startDate)
                .endDate(endDate)
                .build());

        eventRepository.save(Event.builder()
                .title("주간 인기 TOP 10")
                .description("이번 주 가장 많은 독자가 선택한 인기 웹툰 TOP 10을 확인하세요!")
                .bgColor("#2d1b69")
                .linkUrl("/popular")
                .startDate(startDate)
                .endDate(endDate)
                .build());

        eventRepository.save(Event.builder()
                .title("완결 웹툰 정주행 특집")
                .description("완결된 명작 웹툰을 한 번에 정주행하세요! 다양한 장르의 완결 웹툰이 준비되어 있습니다.")
                .bgColor("#4a1942")
                .linkUrl("/completed")
                .startDate(startDate)
                .endDate(endDate)
                .build());

        eventRepository.save(Event.builder()
                .title("매일 코인 충전 이벤트")
                .description("매일 로그인하고 코인을 충전하세요! 충전 금액의 50% 보너스 코인을 드립니다.")
                .bgColor("#4a3000")
                .linkUrl("/mypage/charge")
                .startDate(startDate)
                .endDate(endDate)
                .build());

        eventRepository.save(Event.builder()
                .title("신규 가입 50 코인 지급!")
                .description("지금 KYUNGWON TOON에 가입하면 50 코인을 즉시 지급해 드립니다!")
                .bgColor("#0a3354")
                .linkUrl("/signup")
                .startDate(startDate)
                .endDate(endDate)
                .build());
    }

    // ===== 헬퍼 메서드 =====

    private Comic buildComic(String title, String author, String description,
                              ComicStatus status, int freeEpisodeCount,
                              Set<DayOfWeek> days, Set<String> genreNames,
                              int episodeCount, int coinPrice, String rating, String seed) {
        Set<Genre> genreSet = new java.util.HashSet<>();
        for (String name : genreNames) {
            genreSet.add(genres.get(name));
        }
        Comic comic = createComic(title, author, description, status, freeEpisodeCount, days, genreSet);
        createEpisodes(comic, episodeCount, coinPrice,
                "https://picsum.photos/seed/" + seed + "ep%d");
        if (rating != null) {
            comic.updateAverageRating(new BigDecimal(rating));
        }
        return comic;
    }

    private Comic createComic(String title, String author, String description,
                               ComicStatus status, int freeEpisodeCount,
                               Set<DayOfWeek> days, Set<Genre> genres) {
        Comic comic = Comic.builder()
                .title(title)
                .author(author)
                .description(description)
                .thumbnail("https://picsum.photos/seed/" + title.hashCode() + "/300/400")
                .freeEpisodeCount(freeEpisodeCount)
                .status(status)
                .build();
        comic.updateDays(days);
        comic.updateGenres(genres);
        return comicRepository.save(comic);
    }

    private void createEpisodes(Comic comic, int count, int paidCoinPrice, String imageUrlPattern) {
        for (int i = 1; i <= count; i++) {
            Episode episode = Episode.builder()
                    .comic(comic)
                    .episodeNumber(i)
                    .title(i + "화")
                    .thumbnail("https://picsum.photos/seed/" + comic.getTitle().hashCode() + "ep" + i + "/200/200")
                    .coinPrice(i <= comic.getFreeEpisodeCount() ? 0 : paidCoinPrice)
                    .build();
            episodeRepository.save(episode);

            // 각 에피소드에 이미지 5장
            for (int j = 1; j <= 5; j++) {
                episodeImageRepository.save(EpisodeImage.builder()
                        .episode(episode)
                        .imageUrl(String.format(imageUrlPattern, i) + "p" + j + "/800/1200")
                        .order(j)
                        .build());
            }
        }
    }
}
