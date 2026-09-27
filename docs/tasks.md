# 🏠 Zadanie domowe – Wielowątkowość

`Thread` · `synchronized` · pola atomowe · `ExecutorService` · `CompletableFuture`

---

## Przed rozpoczęciem

### Materiał podstawowy

Zadania opierają się **wyłącznie** na materiale z zajęć:
`kurs_zajecia/0_matriały/watki/watki.pdf`. Wszystko, co jest potrzebne do rozwiązania, znajdziesz
tam — razem z prawdziwymi wydrukami z konsoli. Nie musisz szukać niczego w internecie.

Mapa: które rozdziały materiału obsługują które zadanie.

| Zadanie | Rozdziały materiału |
| --- | --- |
| 1 – licznik wątków | 2 (pierwszy wątek), 2.1 (`start()` vs `run()`), 4.1 (`sleep`/`join`), 5 (wyścig), 8.2 (`AtomicInteger`) |
| 2 – suma tablicy | 3 (co wspólne, co własne), 5 (utrata zapisów), 7 (`synchronized`), 8.2 (`AtomicLong`), 9.2 (confinement) |
| 3 – pula wątków | 13, 13.1–13.3 (pule, zamykanie), 13.4 (`Callable`/`Future`), 13.5 (`invokeAll`) |
| 4 – bank | 7 (`synchronized`), 8.3–8.4 (CAS, `AtomicReference`), 11.1–11.3 (deadlock i lekarstwo), 12 (`ReentrantLock`, `tryLock`), 14.1/14.3/14.5 (`CompletableFuture`, `allOf`, własna pula), 16 (wybór struktury na konta) |

### Lektura dodatkowa (opcjonalnie, po materiale)

* podstawy wątków: <https://www.geeksforgeeks.org/java/java-multithreading-tutorial/>
* podstawy wątków, inne ujęcie: <https://www.digitalocean.com/community/tutorials/multithreading-in-java>
* pula wątków: <https://www.baeldung.com/java-executor-service-tutorial>
* `CompletableFuture`: <https://www.baeldung.com/java-completablefuture>
* kolekcje współbieżne (przydaje się w zadaniu 4, przy wyborze struktury na konta):
  <https://medium.com/@rohitpatil3898/concurrent-collection-in-java-1c97ad28fed2>
  oraz <https://www.baeldung.com/java-synchronized-collections>

### Jak pracować z tym zadaniem

1. Każde zadanie to **osobna klasa z własną metodą `main`** — łatwiej uruchamiać i porównywać.
   Zadanie 4 to kilka klas w jednym pakiecie, np. `bank`.
2. W treści zadań dostajesz **szkielety**: nazwy klas, pola i sygnatury metod z `// TODO`.
   Ciała metod piszesz sam. To jest właśnie ćwiczenie — gotowe rozwiązanie znalezione w materiale
   i przeklejone niczego nie nauczy.
3. Pod każdym szkieletem jest sekcja **„Co jest czym"** — tłumaczy każdy element użytego API:
   co to jest, co robi, jak się tego wywołuje i jaki błąd popełnia się tu najczęściej.
4. Programy współbieżne **uruchamiaj po kilka razy** (`Ctrl+F5` / `⌘R`). Jeden przebieg nic nie
   dowodzi: kod z wyścigiem potrafi dać poprawny wynik kilka razy pod rząd. Jeden zły wynik na
   dziesięć przebiegów to błąd, nie „dziwny przypadek".
5. Wymagana Java: **17+**. Jeśli masz Javę 19 lub nowszą, `ExecutorService` da się użyć
   w `try-with-resources` (jest `AutoCloseable`) — w materiale, rozdział 13.4, jest przykład.

---

# Zadanie 1 – Prosty licznik wątków

**Cel:** uruchomić własne wątki, poczekać na nie i zobaczyć na własne oczy, że kolejność wydruków
nie jest przewidywalna.

## Opis

Napisz program, który:

* tworzy **N** wątków (np. **5**),
* każdy wątek wypisuje najpierw swój numer (np. `Wątek 1 start`), a potem w pętli liczby
  od **1** do **10** z krótkim opóźnieniem (`Thread.sleep(100)`),
* **główny wątek** czeka, aż wszystkie się zakończą, i dopiero wtedy wypisuje:
  `Wszystkie wątki zakończyły działanie.`

## Szkielet do uzupełnienia

```java
public class CounterTask implements Runnable {

    private final int number;              // numer TEGO wątku – przekazany w konstruktorze

    public CounterTask(int number) {
        // TODO
    }

    @Override
    public void run() {
        // TODO: wypisz numer, potem pętla 1..10 z Thread.sleep(100)
    }

    public static void main(String[] args) throws InterruptedException {
        // TODO: utwórz N wątków, uruchom je, poczekaj na wszystkie, wypisz komunikat końcowy
    }
}
```

## Co jest czym

* **`Runnable`** – interfejs z jedną metodą `run()`. To „opis pracy do wykonania", sam z siebie
  nie tworzy żadnego wątku. Wątek dopiero go dostaje: `new Thread(zadanie)`.
* **`new Thread(runnable, "nazwa")`** – tworzy wątek i **opcjonalnie** nadaje mu nazwę. Nazwa jest
  darmowa i bardzo pomaga w debugowaniu — widać ją potem w `jstack` i w debuggerze IntelliJ.
* **`t.start()`** – prosi system operacyjny o **nowy wątek** i uruchamia w nim `run()`.
  **`t.run()`** – zwykłe wywołanie metody, **w bieżącym wątku**. Program działa, kompiluje się,
  nie ma żadnej równoległości. To błąd numer jeden w tym temacie (materiał, rozdział 2.1).
* **`Thread.sleep(100)`** – usypia **wątek, który to wywołuje**, na 100 ms. Metoda jest statyczna,
  więc `t.sleep(100)` uśpi ten wątek, w którym piszesz, a nie `t` — mylące, nie używaj tej formy.
  Rzuca `InterruptedException` (sprawdzany), więc albo `throws`, albo `try/catch`.
* **`t.join()`** – blokuje wątek wołający **do zakończenia** wątku `t`. Aby poczekać na wszystkie,
  trzymaj wątki w tablicy/liście i wywołaj `join()` na każdym w drugiej pętli.
* **numer w konstruktorze** – pole `final int number` jest ustawiane **przed** `start()`, więc każdy
  wątek widzi swoją własną, niezmienną wartość. Gdybyś zamiast tego sięgał w `run()` po zmienną
  pętli `i` z zewnątrz, numery by się mieszały.

## Kolejność wywołań, która ma znaczenie

```java
for (Thread t : threads) t.start();    // najpierw uruchom WSZYSTKIE
for (Thread t : threads) t.join();     // dopiero potem czekaj na WSZYSTKIE
```

Jeśli połączysz to w jedną pętlę (`start(); join();`), program stanie się z powrotem
**sekwencyjny** — każdy wątek zaczeka na poprzednika. Uruchom obie wersje i porównaj czas.

## Jak sprawdzić, że jest dobrze

* Liczby z różnych wątków **przeplatają się** i przy każdym uruchomieniu inaczej — to jest poprawne
  zachowanie, nie błąd.
* Komunikat `Wszystkie wątki zakończyły działanie.` jest **zawsze ostatni**. Jeśli wypisuje się
  w środku, zgubiłeś `join()`.

## Dodatkowe wyzwanie (opcjonalnie)

1. Dodaj wspólne pole `static int started` i zwiększaj je w każdym wątku (np. 100 000 razy
   w pętli). Uruchom 5 razy i **zapisz wyniki** — będą mniejsze od oczekiwanej wartości i za
   każdym razem inne.
2. Napraw to na dwa sposoby: `synchronized` na metodzie zwiększającej, a potem `AtomicInteger`
   i `incrementAndGet()`. Porównaj: obie wersje są poprawne, ale jedna zakłada zamek, a druga nie.
   Dlaczego wynik bez synchronizacji jest **za mały**, a nigdy za duży — wyjaśnia rozdział 5.1.

---

# Zadanie 2 – Suma dużej tablicy w wielu wątkach

**Cel:** podzielić pracę na fragmenty tak, żeby wątki **nie musiały** się w ogóle synchronizować,
i sprawdzić wynik czymś, co nie kłamie — porównaniem z wersją jednowątkową.

## Opis

Masz tablicę `int[]` o długości **1 000 000**, wypełnioną losowymi wartościami. Napisz program, który:

* dzieli tablicę na **K** fragmentów (zacznij od **4**),
* każdy fragment liczy **osobny wątek**,
* po zakończeniu wszystkich wątków sumuje wyniki częściowe,
* wypisuje sumę i **porównuje** ją z sumą policzoną zwykłą pętlą w jednym wątku.

## Szkielet do uzupełnienia

```java
public class ParallelSum {

    private static final int SIZE = 1_000_000;
    private static final int PARTS = 4;

    /** Zadanie liczące sumę fragmentu [from, to) i zapisujące ją pod swój indeks. */
    static class PartialSumTask implements Runnable {
        private final int[] data;
        private final int from;             // włącznie
        private final int to;               // wyłącznie
        private final long[] results;       // WSPÓLNA tablica wyników
        private final int index;            // MÓJ i tylko mój indeks w results

        // TODO: konstruktor

        @Override
        public void run() {
            // TODO: policz sumę data[from..to) i zapisz do results[index]
        }
    }

    static long sequentialSum(int[] data) {
        // TODO: zwykła pętla, wynik odniesienia
        return 0;
    }

    public static void main(String[] args) throws InterruptedException {
        // TODO: wypełnij tablicę, podziel na PARTS, uruchom wątki, poczekaj, zsumuj, porównaj
    }
}
```

## Co jest czym

* **`long[] results` + własny indeks** – najtańsza poprawna synchronizacja to **jej brak**.
  Każdy wątek pisze **tylko** pod `results[index]`, więc dwa wątki nigdy nie dotykają tej samej
  komórki i nie ma czego chronić. To jest wzorzec **confinement** z rozdziału 9.2.
  Widoczność zapisów gwarantuje `join()`: po powrocie z `join()` wątek główny widzi wszystko,
  co zrobił wątek zakończony.
* **dlaczego `long`, a nie `int`** – milion liczb typu `int` bez trudu przekroczy zakres `int`
  (±2,1 mld). Przepełnienie w Javie **nie rzuca wyjątku**, tylko cicho zawija wynik na minus.
  To najczęstszy błąd w tym zadaniu i wygląda jak błąd synchronizacji, którym nie jest.
* **granice fragmentu** – policz je z `SIZE` i `PARTS` tak, żeby fragmenty **stykały się bez luk
  i bez nakładek**. Uwaga na resztę z dzielenia: przy `SIZE = 1_000_003` i `PARTS = 4` ostatni
  fragment musi sięgać do końca tablicy. Sprawdzenie: suma długości fragmentów == `SIZE`.
* **`AtomicLong` – wariant alternatywny** – jeśli wolisz jeden wspólny licznik, użyj
  `AtomicLong total` i **jednego** wywołania `total.addAndGet(mojaCzesciowa)` na koniec pracy
  wątku. Kluczowe: sumuj lokalnie w zwykłej zmiennej `long`, a do pola atomowego wejdź **raz**.
  Wywołanie `addAndGet` milion razy w pętli jest poprawne, ale wielokrotnie wolniejsze niż wersja
  jednowątkowa — i to też warto zmierzyć.
* **`synchronized` – wariant trzeci** – wspólny licznik chroniony blokiem. Ta sama zasada:
  wchodź do bloku raz, z gotową sumą częściową, a nie w każdej iteracji.
* **porównanie z wersją sekwencyjną** – to jest **test** tego zadania. Sumę liczoną równolegle
  i sekwencyjnie porównaj operatorem `==` na `long`. Jeśli się różnią, masz albo wyścig,
  albo dziurę w podziale fragmentów.

## Jak sprawdzić, że jest dobrze

* `sumaRownolegla == sumaSekwencyjna` przy **każdym** z co najmniej 5 uruchomień.
* Wypisz też liczbę fragmentów i granice każdego z nich — dziura w podziale to najczęstsza
  przyczyna „prawie dobrego" wyniku.

## Dodatkowe wyzwania (opcjonalnie)

1. Zmierz czas obu wersji przez `System.nanoTime()` (nie `currentTimeMillis()` — ma zbyt małą
   rozdzielczość na krótkie pomiary). Wykonaj pomiar kilka razy w jednym uruchomieniu:
   pierwszy przebieg jest zawsze najwolniejszy, bo JVM jeszcze nie skompilowała kodu.
2. Sprawdź `PARTS` = 1, 2, 4, 8, 16. Zysk przestaje rosnąć w okolicy liczby rdzeni —
   `Runtime.getRuntime().availableProcessors()` powie ci, ile ich masz.
3. Zmniejsz tablicę do 10 000 elementów i powtórz pomiar. Wersja wielowątkowa może wyjść
   **wolniejsza** — narzut na utworzenie wątków przewyższa zysk. To jest prawidłowy wynik
   i najważniejsza obserwacja w całym zadaniu.

---

# Zadanie 3 – Pula wątków: `ExecutorService`, `Callable`, `Future`

**Cel:** przestać tworzyć wątki ręcznie i zacząć **zlecać zadania**, które zwracają wynik.

## Opis

Symulujemy system przetwarzający zestaw zadań obliczeniowych w tle.

Masz listę liczb `List<Integer>` od **1** do **20**. Dla każdej liczby **n** tworzysz zadanie
(`Callable`), które:

* liczy **silnię** `n!` **albo** wartość **2ⁿ** (wybierz jedno; silnia jest ciekawsza),
* usypia losowo **100–500 ms**, żeby zasymulować różny czas przetwarzania,
* zwraca wynik.

Zadania wykonuje pula o **4** wątkach. Po zebraniu wszystkich wyników wypisz pary `n -> wynik`
(np. `5 -> 120`) **w kolejności rosnącej n**, i zamknij pulę.

## Szkielet do uzupełnienia

```java
public class FactorialPool {

    private static final int POOL_SIZE = 4;
    private static final int MAX_N = 20;

    /** Liczy n! i zwraca wynik; w środku losowy sleep 100–500 ms. */
    static BigInteger factorial(int n) {
        // TODO
        return BigInteger.ONE;
    }

    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);
        List<Future<BigInteger>> futures = new ArrayList<>();

        // TODO 1: dla każdego n złóż zadanie do puli i zapamiętaj Future

        // TODO 2: w DRUGIEJ pętli odbierz wyniki i wypisz "n -> wynik"

        // TODO 3: zamknij pulę
    }
}
```

## Co jest czym

* **`ExecutorService`** – „menedżer wątków". Ty dajesz mu zadania, on decyduje, który wątek je
  wykona. Wątki są **ponownie używane** — 20 zadań na puli 4 wątków to 4 wątki, nie 20.
* **`Executors.newFixedThreadPool(4)`** – pula o stałym rozmiarze. Zadań może czekać dowolnie
  wiele, ale jednocześnie pracuje maksymalnie 4. Inne rodzaje pul: rozdział 13.1.
* **`Callable<T>`** – jak `Runnable`, ale metoda `call()` **zwraca `T`** i może rzucić wyjątek
  sprawdzany. Jako lambda: `() -> factorial(n)`. Kompilator sam rozpozna `Callable` zamiast
  `Runnable`, bo lambda zwraca wartość.
* **`pool.submit(zadanie)`** – oddaje zadanie do puli i **natychmiast** zwraca `Future<T>`.
  Nie blokuje. To jest tylko pokwitowanie.
* **`Future<T>`** – pokwitowanie na wynik, którego jeszcze nie ma. Analogia z materiału: numerek
  z pizzerii. Metody: `get()` (czeka na wynik), `get(2, TimeUnit.SECONDS)` (czeka z limitem,
  rzuca `TimeoutException`), `isDone()` (czy gotowe, bez blokowania).
* **`get()` blokuje** – i tu jest **pułapka tego zadania**:

  ```java
  for (int n = 1; n <= MAX_N; n++) {
      Future<BigInteger> f = pool.submit(() -> factorial(n));
      System.out.println(n + " -> " + f.get());   // ❌ blokujesz po KAŻDYM zadaniu
  }
  ```

  Ta pętla jest **sekwencyjna**, mimo puli wątków: czekasz na wynik 1, zanim zlecisz zadanie 2.
  Poprawnie: **pierwsza pętla** wysyła wszystkie zadania do listy `Future`, **druga** zbiera wyniki.
  Różnicę zobaczysz w czasie działania — patrz „Jak sprawdzić".
* **wyjątek z zadania** – nie ginie, ale **zmienia typ**. `get()` rzuca `ExecutionException`,
  a prawdziwa przyczyna siedzi w `e.getCause()`. Jeśli o tym zapomnisz, w logu będzie bezużyteczne
  „ExecutionException".
* **`pool.shutdown()`** – **obowiązkowe**. Wątki puli są domyślnie nie-daemon, więc bez tego JVM
  nie zakończy programu i konsola w IntelliJ zostanie „zawieszona". `shutdown()` nie przerywa
  zadań już zaczętych — tylko nie przyjmuje nowych. Wariant `shutdownNow()` przerywa,
  `awaitTermination(...)` czeka na dokończenie.
* **`invokeAll(lista)` – wariant alternatywny** – gdy masz od razu całą listę zadań, jedno
  wywołanie zleca je wszystkie i **blokuje do zakończenia wszystkich**, zwracając
  `List<Future<T>>` w tej samej kolejności, w jakiej były zadania. Kolejność wyników jest wtedy
  gwarantowana i nie musisz jej sortować.
* **`BigInteger`** – liczba całkowita bez ograniczenia zakresu. `20!` jeszcze wchodzi w `long`,
  ale `21!` już nie — i przepełni się **bez ostrzeżenia**. Podstawy:
  `BigInteger.ONE`, `BigInteger.valueOf(n)`, `a.multiply(b)` (zwraca **nowy** obiekt — `BigInteger`
  jest niemutowalny, `a.multiply(b)` samo z siebie nie zmienia `a`), `BigInteger.TWO.pow(n)` dla 2ⁿ.
* **losowy sleep** – użyj `ThreadLocalRandom.current().nextInt(100, 501)`, a nie wspólnego obiektu
  `Random` — `ThreadLocalRandom` daje każdemu wątkowi własny generator, bez konkurencji o jeden
  obiekt. Zwróć uwagę, że górna granica w `nextInt(a, b)` jest **wyłączna**.

## Jak sprawdzić, że jest dobrze

* Sprawdź kilka wyników ręcznie: `5 -> 120`, `10 -> 3628800`, a dla 2ⁿ: `10 -> 1024`.
* Zmierz całkowity czas. Przy 20 zadaniach, sleepie średnio 300 ms i puli 4 wątków powinno wyjść
  rzędu **1,5 s**. Jeśli wychodzi ~6 s, wywołujesz `get()` w pętli zlecającej.
* Wypisz w zadaniu `Thread.currentThread().getName()` — zobaczysz, że powtarza się tylko
  kilka nazw `pool-1-thread-N`. To dowód, że wątki są używane ponownie.
* Program **kończy się sam**, bez klikania „stop". Jeśli nie — brakuje `shutdown()`.

---

# Zadanie 4 – Symulacja banku wielowątkowego

**Cel:** złożyć w jedną całość `synchronized`, `ReentrantLock`, `AtomicReference` i `CompletableFuture`,
a przy okazji zrozumieć, dlaczego przelew między dwoma kontami jest klasycznym źródłem zakleszczenia.

To zadanie jest **wyraźnie trudniejsze** od poprzednich i pochłonie najwięcej czasu.
Rób je etapami: najpierw punkt 1 i 2 z prostą, jednowątkową `main`, i dopiero gdy to działa,
dokładaj punkt 5, a wersje alternatywne (3 i 4) na końcu.

## Warunki, które musi spełnić gotowe rozwiązanie

* wiele wątków równolegle wykonuje **wpłaty, wypłaty i przelewy**,
* przy **1000 losowych transakcji suma sald wszystkich kont jest stała** — to niezmiennik bankowy
  i jednocześnie jedyny wiarygodny test tego zadania,
* program **nigdy się nie zawiesza**, mimo że przelew blokuje **dwa** konta naraz,
* synchronizację realizujesz **trzema** mechanizmami w trzech wariantach klasy konta:
  `synchronized`, `ReentrantLock`, `AtomicReference`.

---

## 1. Klasa `BankAccount` (wariant `synchronized`)

```java
public class BankAccount {

    private final Long id;
    private long balanceGr;                 // saldo w GROSZACH

    public BankAccount(Long id, long initialPln) {
        // TODO: zapamiętaj id, przelicz złote na grosze
    }

    public synchronized void deposit(long amountGr) {
        // TODO: odrzuć amountGr <= 0 (IllegalArgumentException), dodaj do salda
    }

    public synchronized void withdraw(long amountGr) {
        // TODO: odrzuć amountGr <= 0; jeśli brakuje środków -> InsufficientFundsException;
        //       w przeciwnym razie odejmij
    }

    public synchronized long getBalanceGr() {
        // TODO
        return 0;
    }

    public Long getId() {
        return id;                          // pole final – synchronizacja niepotrzebna
    }
}
```

Do tego własny wyjątek:

```java
/** Nieoznaczony (unchecked) – nie zmusza do try/catch w każdej lambdzie. */
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(Long accountId, long balanceGr, long requestedGr) {
        // TODO: super(...) z czytelnym komunikatem zawierającym wszystkie trzy wartości
    }
}
```

### Co jest czym

* **`long` w groszach, nie `double` w złotówkach** – `double` nie potrafi dokładnie zapisać
  `0.10`, więc po tysiącu operacji suma sald przestanie się zgadzać na ostatnich miejscach
  po przecinku i **niezmiennik pęknie bez żadnego wyścigu**. Zdiagnozowanie tego jako błędu
  zaokrągleń, a nie błędu współbieżności, zajmuje ludziom godziny. Cała arytmetyka na `long`
  w groszach, a złotówki tylko przy wypisywaniu.
* **`public synchronized void deposit(...)`** – to skrót od `synchronized (this) { ... }`:
  wątek wchodzący do metody zajmuje monitor **tego konkretnego obiektu konta**. Dwa różne konta
  mają dwa różne monitory, więc operacje na koncie 1 i koncie 7 dzieją się równolegle —
  i tak ma być.
* **dlaczego `getBalanceGr()` też jest `synchronized`** – nie chodzi o „psucie" salda przy
  czytaniu, a o **widoczność**: bez wejścia w monitor nie masz gwarancji, że zobaczysz ostatni
  zapis wykonany przez inny wątek (materiał, rozdział 6). Zsynchronizowanie połowy dostępów
  to jeden z najczęstszych błędów — rozdział 22.2.
* **`getId()` bez `synchronized`** – pole jest `final`, ustawiane w konstruktorze i nigdy nie
  zmieniane. Takie pola są bezpieczne z definicji (rozdział 9.1).
* **kolejność w `withdraw`: najpierw sprawdź, potem odejmij** – i **oba kroki w tym samym**
  bloku `synchronized`. Gdyby sprawdzenie było poza blokiem, dostałbyś podręcznikowe
  „sprawdź, potem zrób" z rozdziału 8.3 i saldo mogłoby zjechać poniżej zera.
* **wyjątek rzucany *przed* zmianą salda** – to on utrzymuje niezmiennik przy braku środków:
  skoro nic nie odjęto, nie ma czego dodawać na drugim koncie.

---

## 2. Klasa `Bank`

```java
public class Bank {

    // TODO: struktura danych przechowująca konta, dostępna po id
    //       (wybór struktury jest częścią zadania – patrz "Co jest czym")

    public void openAccount(long id, long initialPln) {
        // TODO
    }

    /** Atomowy przelew: albo obie strony, albo żadna. NIE może się zakleszczyć. */
    public void transfer(long fromId, long toId, long amountGr) {
        // TODO
    }

    /** Suma sald wszystkich kont w groszach. */
    public long totalBalanceGr() {
        // TODO
        return 0;
    }
}
```

### Co jest czym

* **`// TODO: struktura danych`** – **wybór jest częścią zadania.** Nie podaję gotowej klasy;
  odpowiedz sobie najpierw na cztery pytania i dopiero potem sięgnij po rozdział 16 materiału,
  gdzie każdy kandydat jest opisany razem z ceną, jaką za niego płacisz:
  1. **Czego potrzebujesz od tej struktury?** `transfer` dostaje `fromId` i `toId` i musi z nich
     dostać obiekty kont. Czy to jest dostęp po **kluczu**, po **indeksie**, czy przeglądanie
     po kolei? Jaka złożoność wyjdzie przy każdej z tych opcji, jeśli kont będzie 10 000?
  2. **Kto i kiedy do niej pisze, a kto czyta?** Konta zakładasz raz, na starcie, jednym wątkiem
     — ale `get(...)` woła potem 8 wątków naraz, tysiąc razy. Czy w tym rozkładzie ruchu
     w ogóle potrzebujesz struktury współbieżnej, czy wystarczy zwykła, gdy tylko zagwarantujesz,
     że jest **kompletna i już nigdy niezmieniana**, zanim wystartuje pierwszy wątek?
  3. **Co się stanie, jeśli wybierzesz źle?** Sprawdź w rozdziale 16.1, co dzieje się ze zwykłą
     `HashMap`, do której piszą dwa wątki — to nie jest „zły wynik", to trwałe uszkodzenie
     struktury. A w 16.4 zobacz, czym różni się od tego opakowanie `Collections.synchronizedXxx`
     i ile kosztuje.
  4. **Czy da się to zamknąć?** Gdyby konta ustalać wyłącznie w konstruktorze i nigdy potem nie
     dodawać, jaki wariant byłby najbezpieczniejszy z możliwych? (Podpowiedź: rozdziały 9.1 i 9.2
     mówią o strukturach, których w ogóle nie trzeba chronić.)

  Wybór **uzasadnij w komentarzu klasy** — jedno zdanie, dlaczego ta, a nie inna. Każda
  z sensownych odpowiedzi jest do obrony; nieuzasadniona nie jest.
* **czego ta struktura _nie_ załatwia** – to, że sama struktura jest thread-safe, nie mówi
  **nic** o bezpieczeństwie **obiektów w środku**. Bezpieczny zbiór kont ≠ bezpieczne konta.
  Salda chronisz osobno i sam — struktura pilnuje tylko samej siebie. Nie licz też na to, że
  bezpieczna struktura zrobi z `transfer` operację niepodzielną: dwa oddzielne `get(...)` to
  wciąż dwie operacje.
* **czego wymaga `transfer`** – dwie operacje na dwóch różnych obiektach mają wyglądać dla reszty
  świata jak jedna. Nie wystarczy, że `withdraw` i `deposit` są osobno `synchronized`: między nimi
  jest szczelina, w której pieniądze „nie istnieją" na żadnym koncie. Dlatego przelew musi zająć
  **oba** monitory na czas całej operacji.
* **dlaczego to grozi zakleszczeniem** – wątek A przelewa 1→2 i bierze monitor konta 1, wątek B
  przelewa 2→1 i bierze monitor konta 2. Teraz każdy czeka na to, co trzyma drugi. Program stoi
  **na zawsze**, bez wyjątku i bez żadnego wpisu w logu. Materiał pokazuje to na działającym
  przykładzie w rozdziale 11.1.
* **jak temu zapobiec — to jest sedno zadania.** Zakleszczenie wymaga **cyklu** oczekiwania.
  Cykl da się uczynić niemożliwym, jeśli **wszystkie wątki zajmują zamki w tej samej,
  z góry ustalonej kolejności** — wtedy nie ma sytuacji „ja mam pierwszy, ty masz drugi".
  Pytanie, na które musisz sobie odpowiedzieć: **jaka niezmienna cecha konta może wyznaczyć
  tę globalną kolejność?** Gotowy wzorzec (i uzasadnienie) jest w rozdziale 11.3 — przeczytaj,
  zamknij materiał i napisz to sam dla swoich klas.
* **`synchronized` jest reentrantny** – jeśli w `transfer` zajmiesz monitory kont z zewnątrz
  (`synchronized (konto) { ... }`), a w środku wywołasz `withdraw()`, które samo jest
  `synchronized`, **nie zablokujesz się sam**. Wątek, który już trzyma monitor, wchodzi w niego
  ponownie bez czekania (rozdział 7.2).
* **przypadek `fromId == toId`** – obsłuż go osobno na wejściu. Przelew z konta na to samo konto
  nie ma sensu, a przy okazji rozbija logikę porządkowania zamków (nie ma „mniejszego" i
  „większego").
* **`totalBalanceGr()`** – w tym zadaniu wołasz ją **po** zakończeniu wszystkich transferów,
  więc zwykła pętla po wszystkich kontach z sumowaniem `getBalanceGr()` wystarczy.
  Warto jednak wiedzieć, że wywołana **w trakcie** pracy wątków może zwrócić sumę, która nie
  odpowiada żadnej rzeczywistej chwili — bo każde konto czyta się osobno, a między odczytami
  ktoś zdąży przelać. Zsynchronizowanie samej metody tego **nie naprawia**. Zastanów się, co by
  było potrzebne (podpowiedź: zamek obejmujący wszystkie konta albo migawka całego banku) —
  odpowiedz w komentarzu, implementować nie musisz.

---

## 3. Wariant alternatywny – `ReentrantLock` z `tryLock`

Napisz `BankAccountLock`: to samo konto, ale zamiast `synchronized` ma **jawny zamek**.

```java
public class BankAccountLock {

    private final Long id;
    private final ReentrantLock lock = new ReentrantLock();
    private long balanceGr;

    // TODO: konstruktor

    /** Wywołujący MUSI trzymać lock tego konta. */
    void depositUnlocked(long amountGr)  { /* TODO */ }
    void withdrawUnlocked(long amountGr) { /* TODO */ }

    ReentrantLock lock() { return lock; }
    long balanceGr()     { /* TODO */ return 0; }
}
```

Metoda przelewu ma zwracać `boolean` — `true` gdy się udało, `false` gdy **zrezygnowała**.
Algorytm, który masz zaimplementować, słowami:

1. Policz **termin** (deadline), do którego wolno próbować — np. `System.currentTimeMillis() + 1000`.
2. Dopóki nie minął termin: spróbuj zająć zamek konta źródłowego **z limitem czasu** (np. 50 ms).
3. Jeśli się udało — spróbuj zająć, też z limitem, zamek konta docelowego.
4. Jeśli udało się zająć **oba** — wykonaj wypłatę i wpłatę, zwolnij oba zamki, zwróć `true`.
5. Jeśli **drugi** zamek się nie udał — zwolnij pierwszy (to jest krok, który rozrywa cykl!),
   odczekaj chwilę (`Thread.sleep(1)`) i wróć do punktu 2.
6. Jeśli termin minął — zwróć `false`.

### Co jest czym

* **`ReentrantLock`** – robi to samo co `synchronized`, ale jest **zwykłym obiektem z metodami**,
  więc można go *pytać*, *próbować* i *porzucać*. `synchronized` jest bezwarunkowy: raz wszedłeś
  w oczekiwanie i nie ma z niego wyjścia (tabela porównawcza: rozdział 12).
* **`lock.lock()` / `lock.unlock()`** – zajmij / zwolnij. **`unlock()` zawsze w `finally`**:
  jeden wyjątek w sekcji krytycznej bez `finally` i zamek zostaje zajęty **do końca życia
  procesu**, a każdy kolejny wątek zawiesza się na nim na zawsze.
* **`lock.tryLock()`** – próbuje zająć **bez czekania**; zwraca `true`/`false` natychmiast.
* **`lock.tryLock(50, TimeUnit.MILLISECONDS)`** – czeka **maksymalnie** 50 ms. Rzuca
  `InterruptedException`, więc metoda potrzebuje `throws` albo `try/catch`.
* **`unlock()` tylko wtedy, gdy `tryLock` zwrócił `true`** – zwalnianie zamka, którego nie masz,
  rzuca `IllegalMonitorStateException`. Stąd układ: `if (tryLock(...)) { try { ... } finally { unlock(); } }`
  — `finally` **wewnątrz** `if`, nigdy na zewnątrz.
* **dlaczego ten wariant nie potrzebuje porządkowania po `id`** – bo zamiast unikać cyklu,
  **wychodzi z niego**: wątek, któremu nie udał się drugi zamek, oddaje pierwszy i próbuje od nowa.
  To inna strategia niż w punkcie 2 i warto rozumieć obie.
* **`Thread.sleep(1)` między próbami (backoff)** – bez tego dwa wątki mogą wpaść w rytm, w którym
  wciąż odbierają sobie zamki i żaden nie kończy (to **livelock**: procesor pracuje, postępu nie ma
  — rozdział 11.4). Krótka, nierówna pauza rozbija ten rytm.
* **co zrobić z `false`** – zlicz rezygnacje w `AtomicInteger` i wypisz na koniec. Przelew,
  który się nie odbył, **nie łamie** niezmiennika: suma sald nadal musi się zgadzać.
  Za to duża liczba rezygnacji to sygnał, że limity czasu są za krótkie.
* **`depositUnlocked` / `withdrawUnlocked`** – nazwa jest celowo niewygodna, żeby przypominała
  o kontrakcie: te metody **same nie zakładają zamka**, wolno je wołać tylko trzymając zamek konta.
  Taki kontrakt zawsze opisuj w Javadocu — kompilator go nie wymusi.

---

## 4. Wariant alternatywny – `AtomicReference` i migawka stanu

Napisz `BankAccountAtomic`, w którym saldo **nie jest polem do modyfikacji**, tylko siedzi
w niemutowalnej migawce podmienianej w całości.

```java
public class BankAccountAtomic {

    /** Niemutowalna migawka stanu konta. */
    private record Snapshot(long balanceGr, long version) {}

    private final AtomicReference<Snapshot> state;

    public BankAccountAtomic(long initialPln) {
        this.state = new AtomicReference<>(new Snapshot(initialPln * 100, 0));
    }

    public void deposit(long amountGr) {
        // TODO: jedna operacja updateAndGet – zbuduj nową migawkę na podstawie starej
    }

    /** @return true gdy wypłacono, false gdy brak środków. */
    public boolean tryWithdraw(long amountGr) {
        // TODO: ręczna pętla CAS
        //   1. odczytaj bieżącą migawkę
        //   2. jeśli brak środków -> return false
        //   3. zbuduj następną migawkę
        //   4. compareAndSet(bieżąca, następna): udało się -> return true;
        //      nie udało się -> ktoś cię wyprzedził, wróć do kroku 1
        return false;
    }

    public long balanceGr() {
        return state.get().balanceGr();
    }
}
```

### Co jest czym

* **`AtomicReference<T>`** – trzyma referencję do obiektu i pozwala ją podmieniać niepodzielnie.
  Czytelnicy widzą **albo stary, albo nowy** obiekt — nigdy stan „w połowie zmieniony".
* **`record Snapshot(...)`** – rekord jest niemutowalny (same pola `final`), więc migawka nadaje się
  do współdzielenia bez żadnej ochrony. **To jest warunek działania całego wzorca**: gdybyś włożył
  do `AtomicReference` zwykłą klasę z setterami i zmieniał jej pola, nie zyskałbyś nic
  — atomowa jest **podmiana referencji**, a nie to, co siedzi w środku (rozdział 8.4).
* **`state.updateAndGet(s -> nowa(s))`** – gotowa pętla CAS schowana w jednej metodzie:
  odczytaj, przelicz, podmień, a jeśli ktoś w międzyczasie zmienił — powtórz.
  **Uwaga:** przekazana lambda może zostać wywołana **więcej niż raz**, więc nie wolno w niej
  robić nic poza obliczeniem nowej wartości. Żadnych wydruków, zapisów do bazy, inkrementacji
  licznika — bo policzą się podwójnie.
* **`compareAndSet(oczekiwana, nowa)`** – „ustaw `nowa`, ale **tylko jeśli** wciąż jest
  `oczekiwana`". Zwraca `true` przy sukcesie, `false` gdy ktoś nas wyprzedził. `false` **nie jest
  błędem** — to cały mechanizm; reagujesz na niego powtórzeniem pętli.
* **dlaczego dla `tryWithdraw` nie wystarczy `updateAndGet`** – bo `updateAndGet` zawsze coś
  ustawia i zwraca nową wartość; nie ma jak przez niego powiedzieć „nie zrobiłem nic, bo brakowało
  środków". Operacja **warunkowa** potrzebuje ręcznej pętli, w której sam widzisz wynik CAS.
* **`compareAndSet` porównuje **referencje**, nie `equals`** – dwie migawki o identycznych polach
  to dla CAS **różne** obiekty. Dlatego wzorzec działa poprawnie także bez pola `version`.
  Pole `version` jest tu po to, żebyś **widział**, ile podmian faktycznie zaszło — wypisz je na
  koniec i porównaj z liczbą operacji na tym koncie.
* **wniosek do zapisania w komentarzu** – `AtomicReference` z niemutowalnym stanem jest elegancki
  i bez zamków, ale dla operacji typu „wypłać, **jeśli** stać" `synchronized`/`Lock` czyta się
  naturalniej. Nie każdy problem jest problemem dla CAS — i o to w tym punkcie chodzi.
* **czego ten wariant nie umie** – niepodzielnego przelewu między **dwoma** kontami.
  CAS działa na **jednej** komórce pamięci. Zastanów się (i odpowiedz w komentarzu), dlaczego
  „`tryWithdraw` na koncie A, potem `deposit` na koncie B" nie jest jedną atomową operacją,
  i co widziałby obserwator, który zsumuje salda dokładnie między tymi dwoma krokami.

---

## 5. Symulacja (`main`)

Parametry:

* **10 kont**, każde z saldem startowym **1000 PLN**,
* **1000 losowych przelewów**: losowe konto źródłowe, losowe **inne** konto docelowe,
  kwota losowa z zakresu **100–500 PLN**,
* przelewy zlecane przez `CompletableFuture.runAsync(..., pool)` na **własnej puli**
  `Executors.newFixedThreadPool(8)`,
* na koniec: wypisz salda wszystkich kont, sumę sald i **werdykt** —
  suma musi wynieść `10 × 1000 PLN`.

### Elementy API, których użyjesz

Zlecenie jednej pracy na **własnej** puli i zebranie wszystkich:

```java
CompletableFuture<Void> one = CompletableFuture.runAsync(() -> { /* jeden przelew */ }, pool);

CompletableFuture<?>[] all = /* ... 1000 sztuk ... */;
CompletableFuture.allOf(all).join();      // czekamy, aż skończą się WSZYSTKIE
```

### Co jest czym

* **`CompletableFuture.runAsync(zadanie, pool)`** – uruchamia `Runnable` asynchronicznie i zwraca
  `CompletableFuture<Void>` (bez wyniku, liczy się efekt uboczny). Dla zadania zwracającego wartość
  byłoby `supplyAsync`.
* **drugi argument `pool` jest tu obowiązkowy** – bez niego zadania lecą do
  `ForkJoinPool.commonPool()`, czyli **jednej puli wspólnej dla całej JVM**, mającej domyślnie
  „liczba rdzeni − 1" wątków. Zapchanie jej zadaniami blokującymi kładzie w prawdziwej aplikacji
  wszystko inne, co jej używa — łącznie ze strumieniami równoległymi (rozdział 14.5).
* **`allOf(...)`** – przyjmuje **varargs**, więc tablica `CompletableFuture<?>[]` wchodzi wprost.
  Zwraca `CompletableFuture<Void>`: mówi tylko „już wszystkie gotowe", **nie zbiera wyników**.
  Tutaj to nie problem, bo przelewy niczego nie zwracają.
* **`.join()`** – czeka na ukończenie. Różnica względem `get()`: `join()` rzuca nieoznaczony
  `CompletionException`, a `get()` — sprawdzane `ExecutionException`/`InterruptedException`.
  W lambdach `join()` jest wygodniejszy, bo nie zmusza do `try`.
* **`InsufficientFundsException` wewnątrz zadania** – część losowych przelewów **musi** się nie
  udać (konto ma 1000 PLN, a przelewy sięgają 500 PLN). Wyjątek przechwyć **w środku lambdy**
  i policz nieudane próby. Jeśli go nie przechwycisz, `CompletableFuture` zapamięta błąd
  i `allOf(...).join()` rzuci `CompletionException` — cała symulacja przerwie się na pierwszej
  nieudanej próbie. Nieudany przelew jest tu **normalnym scenariuszem**, nie awarią.
* **zmienne przekazywane do lambdy muszą być *effectively final*** – zmiennej pętli `i`
  ani zmiennej, do której przypisujesz w kolejnych obrotach, nie wolno użyć wewnątrz lambdy.
  Przepisz je do lokalnych `final` **przed** utworzeniem lambdy (`final long fromF = from;`)
  albo od razu deklaruj jako `final` w ciele pętli. Kompilator to wymusi.
* **`new Random(42)` – ziarno** – ten sam ciąg **transakcji** przy każdym uruchomieniu, co ułatwia
  porównywanie przebiegów. Ale to **nie czyni testu deterministycznym**: kolejność, w jakiej wątki
  te transakcje wykonają, zmienia się za każdym razem. Ziarno powtarza *wejście*, nie *przeplot*.
* **`pool.shutdown()`** – po `join()`. Bez tego program nie zakończy się sam.

### Werdykt na koniec

Wypisz sumę i sprawdź niezmiennik; przy złej sumie **przerwij** program błędem, żeby wynik dało się
zauważyć, a nie przeoczyć w logu:

```java
long expected = 10 * 1000 * 100L;         // 10 kont × 1000 PLN × 100 gr
if (bank.totalBalanceGr() != expected) {
    throw new AssertionError("NIEZMIENNIK ZŁAMANY – gdzieś jest race condition!");
}
```

---

## Jak sprawdzić, że zadanie 4 jest zrobione dobrze

To zadanie **nie jest** zaliczone przez jedno udane uruchomienie. Wykonaj wszystkie cztery próby:

1. **Niezmiennik.** Uruchom **co najmniej 10 razy**. Suma sald musi wynieść dokładnie
   `1 000 000 gr` w **każdym** przebiegu. Jeden zły wynik na dziesięć oznacza wyścig, który
   na produkcji trafi cię w najgorszym możliwym momencie.
2. **Dowód, że synchronizacja jest potrzebna.** Zrób kopię `BankAccount`, usuń z niej wszystkie
   `synchronized`, podnieś liczbę przelewów do 100 000 i uruchom kilka razy. Suma powinna się
   rozjechać. Jeśli nadal się zgadza, zwiększ liczbę wątków i transakcji — to znaczy, że wątki
   po prostu jeszcze się nie rozminęły.
3. **Dowód, że porządkowanie zamków jest potrzebne.** Odwróć kolejność blokowania w `transfer`
   (bierz zawsze najpierw konto źródłowe, potem docelowe) i uruchom symulację. Program powinien
   **stanąć**. Wtedy:
   * `jcmd -l` — znajdź PID procesu Javy,
   * `jcmd <pid> Thread.print` — poszukaj sekcji **`Found one Java-level deadlock`**.

   Zobaczenie tego wydruku raz, na własnym kodzie, jest wartościowsze niż cały rozdział 11.
   Potem przywróć poprawną wersję.
4. **Trzy warianty konta.** Uruchom symulację na każdym z trzech mechanizmów i zanotuj:
   czas działania, liczbę nieudanych przelewów, liczbę rezygnacji w wariancie `tryLock`.

## Co oddajesz

* kod czterech zadań,
* dla zadania 4 krótkie `WNIOSKI.md` (pół strony wystarczy) z odpowiedziami na:
  * jaką strukturę danych wybrałeś na konta i dlaczego akurat tę (co odrzuciłeś i z jakiego powodu),
  * jaka cecha konta wyznaczyła globalną kolejność blokowania i dlaczego akurat ona,
  * dlaczego wariant z `tryLock` nie potrzebuje tej kolejności,
  * dlaczego `AtomicReference` nie wystarcza do przelewu między dwoma kontami,
  * co pokazał `Thread.print` przy celowo wprowadzonym zakleszczeniu.
