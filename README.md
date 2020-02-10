## 1. 생성자 대신 정적 팩터리 메서드를 사용할 수 없는지 생각해 보라

## 왜 써야 할까

Constructor의 한계

 * 생성자는 이름을 지을 수 없음.
 * 생성자는 항상 같은 타입의 Object를 return 함.
 * 생성자는 호출할 때마다 새로운 객체를 생성한다.


## 가독성

``` Java
public class Sandwitch {
    private int tomato;
    private int spam;
    private int chicken;
    private int tuna;
    private int vegitable;

    public Sandwitch(int tomato,int spam,int chicken,int vegitable,int tuna){
        this.tomato = tomato;
        this.spam = spam;
        this.chicken = chicken;
        this.tuna = tuna;
        this.vegitable = vegitable;
    }
    
    // 정적 팩토리 메소드
    public static Sandwitch chickenSandwitch(){
        return new Sandwitch(1,0,2,1,0);
    }

    // 정적 팩토리 메소드
    public static Sandwitch tunaSandwitch(){
        return new Sandwitch(1,0,0,1,2);
    }
}


```

#### 생성자를 통한 객체 생성

``` java
    Sandwitch chickenSandwitch = new Sandwitch(1,0,2,1,0);
    Sandwitch tunaSandwitch = new Sandwitch(1,0,0,1,2);
```

생성자에 전달되는 인자들이 어떤 객체가 생성되는지 설명을 못함.

``` java
    Sandwitch chickenSandwitch = Sandwitch.chickenSandwitch();
    Sandwitch tunaSandwitch = Sandwitch.tunaSandwitch();

```

시그니처가 같은 메소드가 여러개 정의할 필요가 있을 때 어떤 객체를 생성하는지 메소드명으로 추론 가능하여 가독성을 높일 수 있다.

ex) java.text.NumberFormat [NumberFormat](https://docs.oracle.com/javase/8/docs/api/)

## 캐시

Boolean.valueOf(),  Integer.valueOf(), Long.valueOf() 과 같은 메소드는 내부적으로 객체를 캐시 해 놓고 재사용하여 같은 객체가 불필요하게 거듭 생성되는 일을 막는다.

``` java
public static final BigInteger ZERO = new BigInteger(new int[0], 0);

private final static int MAX_CONSTANT = 16;
private static BigInteger posConst[] = new BigInteger[MAX_CONSTANT+1];
private static BigInteger negConst[] = new BigInteger[MAX_CONSTANT+1];

static {
    /* posConst에 1 ~ 16까지의 BigInteger 값을 담는다. */
    /* negConst에 -1 ~ -16까지의 BigInteger 값을 담는다. */
}

public static BigInteger valueOf(long val) {
    // 미리 만들어둔 객체를 리턴한다
    if (val == 0)
        return ZERO;
    if (val > 0 && val <= MAX_CONSTANT)
        return posConst[(int) val];
    else if (val < 0 && val >= -MAX_CONSTANT)
        return negConst[(int) -val];

    // 새로운 객체를 만들어 리턴한다
    return new BigInteger(val);
}

```

## 단점

1. public이나 protected로 선언된 생성자가 없으므로 하위클래스를 만들 수 없다.
2. 정적 팩토리 메서드가 다른 정적 메소드와 확연기 구분되지 않는다. (때문에 명명 규칙을 따르자)

## 의문

* non public class를 반환한다는 것의 이점을 잘 이해하지 못하겠다.

[참고자료1](https://javarevisited.blogspot.com/2017/02/5-difference-between-constructor-and-factory-method-in-java.html)
[참고자료2](https://johngrib.github.io/wiki/static-factory-method-pattern/)

# 2. 생성자 인자가 많을 때는 Builder 패턴 적용을 고려하라

#### 점층적 생성자 패턴 : 생성자들을 쌓아 올리듯 추가하는 것

점층적 생성자 패턴을 사용하면 인자의 순서를 지켜야 하므로 설정할 필요가 없는 필드에도 인자를 전달해야 한다. 이는 클라이언트 코드 작성의 복잡성을 증가시키고 가독성을 떨어뜨린다.

#### 자바 빈 패턴 : 인자 없는 생성자를 호출하여 객체부터 만들고 설정 메서드들을 호출하여 값을 채우는 방식

1. 자바 빈 패턴은 객체 일관성을 일시적으로 깨질 수 있다. 일관성이 깨진 객체는 어디서 사용될까?
2. immutable 클래스를 만들 수 없다.

## 빌더 패턴

1. 필수인자들을 생성자에 전부 전달하여 빌더 객체를 생성.
2. 빌더 객체에 정의된 설정 메서드들을 호출하여 선택적 인자들을 추가.
3. 아무런 인자 없는 build 메소드를 호출하여 immutable 객체 생성.

``` java
public class NutritionFacts {
    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;
    private final int sodium;
    private final int carbohydrate;

    public NutritionFacts(Builder builder) {
        servingSize = builder.servingsSize;
        servings = builder.servings;
        calories = builder.calories;
        fat = builder.fat;
        sodium = builder.sodium;
        carbohydrate = builder.carbohydrate;
    }

    public static class Builder {
        private final int servingsSize;
        private final int servings;
        private int calories = 0;
        private int fat = 0;
        private int sodium = 0;
        private int carbohydrate = 0;

        public Builder(int servingSize, int servings){
            this.servingsSize = servingSize;
            this.servings = servings;
        }

        public Builder calories(int val){
            calories = val;
            return this;
        }

        public Builder fat(int val){
            fat = val;
            return this;
        }

        public Builder carbohydrate(int val){
            carbohydrate = val;
            return this;
        }

        public Builder sodium(int val){
            sodium = val;
            return this;
        }

        public NutritionFacts build(){
            return new NutritionFacts(this);
        }
    }
}


```

빌더 객체는 자기 자신을 반환하므로, 설정 메서드를 호출하는 코드는 죽이어서 쓸 수 있다.

``` java
NutritionFacts cocaCola = new Builder(240,8).calories(100).sodium(35).carbohydrate(27).build();

```

javascript에서는 객체를 인자로 넘겨 값을 설정 할 수 있는데 그것과 비슷한 효과를 내는 것 같다.

빌더 패턴을 사용하면서 불변식 적용

1. build 메서드 안에서 해당 불변식이 위반되었는지 검사
2. 불변식이 적용될 값 전부를 인자로 받는 설정자 메소드 정의

불변식이 위반되면 IllegalStateException을 던진다.

빌더 패턴은 인자가 많은 클래스를 설게할 때, 특히 대부분의 인자가 선택적 인자인 상황에 유용하다.

## 의문 

스프링에서 setter로 의존성 주입을 하는 설정이 있는데 빌더 패턴에서도 적용이 가능할까


# 3. private 생성자나 enum 자료형은 싱글턴 패턴을 따르도록 설계하라

클래스를 싱글턴으로 만들면 클라이언트를 테스트하기가 어렵다??


# 4. 객체 생성을 막을 때는 private 생성자를 사용하라

기본 생성자는 클래스에 생성자가 없을 때 만들어지니까, private 생성자를 클래스에 넣어서 객체 생성을 방지하자.

# 5. 불필요한 객체는 만들지 말라

#### autoboxing : 자바의 기본 자료형과 그 객체 표현형을 섞어 사용할 수 있도록 해줌.

``` java
public static void main(String[] args){
    Long sum = 0L;
    for(long i = 0; i < Integer.MAX_VALUE; i++){
        sum += i;
    }
    System.out.println(sum);
}
```

위 코드에서 sum은 long이 아니라 Long으로 선언되어 있는데 이 때 불필요한 객체가 계속 생성된다. 이 처럼 객체 표현형과 기본자료형의 사용에서 발생하는 버그로 인해 성능저하가 일어날 여지가 있다. 때문에 자동 객체화가 발생하지 않도록 유의해야 한다.

# 6. 유효기간이 지난 객체 참조는 폐기하라

자바는 C/C++ 과 다르게 gc를 이용해 메모리를 관리한다. 하지만 중요한 점은 gc가 메모리를 관리하는 시점을 정확히 제어할 수 없고 모른다는 점이다.
때문에 메모리 관리에 대한 정확한 이해가 없으면 심각한 메모리 누수가 발생할 여지가 있기 때문에 이를 조심해야 한다.

``` java
import java.util.Arrays;
import java.util.EmptyStackException;

public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    
    public Stack(){
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }
    
    public void push(Object e){
        ensureCapacity();
        elements[size++] = e;
    }
    
    public Object pop(){
        if(size == 0)
                throw new EmptyStackException();
        return elements[--size]; // 이 부분에서 메모리누수 발생.
    }
    
    private void ensureCapacity(){
        if(elements.length == size)
            elements = Arrays.copyOf(elements, 2 * size + 1);
    }
    
}


```

#### 만기 참조 : 다시 이용되지 않을 참조를 말한다. 위 예에서 첨자 값이 size보다 작은 곳에 있는 요소들은 실제로 쓰이는 참조들이지만 , 나머지 영역에 있는 참조들은 그렇지 않다. 

만기참조가 존재하면 그 객체를 통해 참조되는 다른 객체들도 gc에서 제외되므로 위험성이 크다.

이를 해결 하기 위해 변수를 정의할 때 그 유효ㅛ범위를 최대한 좁게 만들거나 차선의 방법으로 객체 참조를 null로 처리한다.

예전에 자바스크립트로 코딩할 때 이벤트 리스너를 등록하고 해당 리스너가 중첩되어 발생하는 버그를 직면한적이 있었다. 그 때는 버그로 표면상에 드러나서 파악했지만 버그조차 발생하지 않았으면 심각한 메모리누수가 있었을 것이다. 때문에 콜백함수 같은 경우는 특히 조심하자.

#### 약한참조와 WeekHashMap

## 의문

유효범위를 최대한 좁게 만들면 어떤 원리로 해결되는 것일까

# 7. 종료자 사용을 피하라

** 중요 ** 
C++의 소멸자와 그 개념이 다르다. 소멸자는 생성자와 쌍으로 존재하며 객체에 배정된 자원을 반환하는 수단으로 사용.

종료자는 언제 실행될지 모르는게 가장 큰 단점이다. 따라서 종료자에 긴급한 작업을 처리하면 안된다. ex) 파일 닫기

종료자의 사용보다는 명시적인 종료 메서드를 정의하자. 대신 유효하지 않은 객체임을 표시하는 private 필드를 하나 두고, 모든 메서드 맨 앞에 해당 필드를 검사하는 코드를 두어, 이미 종료된 객체에 메서드를 호출하면 IllegalStateException이 던져지도록 하자.

명시적 종료 메소드는 객체 종료를 보장하기 위해 try-finally 문과 함께 쓰인다.

``` java
  Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    
    try {
        conn = DBPool.getConnection(); //
        stmt = conn.createStatement();
        rs = stmt.executeQuery(..);
        ...
    } catch(SQLException ex) {
        // 예외 처리
    } finally {
        if (rs != null) try { rs.close(); } catch(SQLException ex) {}
        if (stmt != null) try { stmt.close(); } catch(SQLException ex) {}
        if (conn != null) try { conn.close(); } catch(SQLException ex) {}
    }

```

#### 종료자를 사용하는 경우

1. 명시적 종료 메서드 호출을 잊을 경우에 대비하는 안전망으로서의 역할 (반드시 로그를 남길 것)
2. 네이티브 피어???

# 8. equals를 재정의할 때는 일반 규약을 따르라

# 13. 클래스와 멤버의 접근 권한은 최소화 하라

모듈 내부의 데이터를 비롯한 구현 세부사항을 다른 모듈에 잘 감추느냐의 여부가 중요. 잘 설계된 모듈은 고현 세부사항을 전부 API 뒤쪽에 감춘다. 모듈들은 API를 통해서만 서로 통신하며, 각자 내부적으로 무슨 짓을 하는지는 신경쓰지 않는다.

#### 정보 은닉 : 모듈사이의 의존성을 낮춰서, 각자 개별적으로 개발하고, 시험하고, 최적화하고, 이해하고, 변경 할 수 있도록 한다.

같은 패키지 내의 다른 클래스가 반드시 사용해야 하는 멤버인 경우에는 private를 제거해서 해당 멤버를 default로 만들어야 한다. 하지만 그런 변경 작업을 자주 하게 된다면 시스템 설계를 재검토해서 클래스간 의존성을 더 잘 끊어내야한다.

테스트를 위한 것이라도 클래스나 인터페이스, 또는 멤버를 패키지의 공개 API로 만들어서는 곤란하다.

객체 필드는 절대로 pulbic으로 선언하면 안된다.

비-final 필드나 변경 가능 객체에 대한 final 참조 필드를 public으로 선언하면, 필드에 저장될 값을 제한할 수 없게 된다. 따라서 그 필드에 관계된 불변식을 강제할 수 없다.

``` java
package com.java.rule13;

public class Tire {
    public int maxRotation;
    public int accumulatedRoation;
    public String location;

    public Tire(String location,int maxRotation){
        this.location = location;
        this.maxRotation = maxRotation;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public boolean roll(){
        ++accumulatedRoation;
        if(accumulatedRoation < maxRotation){
            System.out.println(location+" Tire 수명: "+(maxRotation-accumulatedRoation)+"회");
            return true;
        }
        else{
            System.out.println("*** "+location+" Tire 펑크 ***");
            return false;
        }
    }
}
// 변경 가능 객체
```



```java
package com.java.rule13;

public class Car {
    public final Tire frontLeftTire = new Tire("앞왼쪽", 6);
    public final Tire frontRightTire = new Tire("앞오른쪽",2);
    public final Tire backLeftTire = new Tire("뒤왼쪽",3);
    public final Tire backRightTire = new Tire("뒤오른쪽",4);

    int run(){
        System.out.println("자동차가 달립니다");
        if(frontLeftTire.roll()==false) return 1;
        if(frontRightTire.roll()==false) return 2;
        if(backLeftTire.roll()==false) return 3;
        if(backRightTire.roll()==false) return 4;
        return 0;
    }
}
```

위 코드는 객체 필드가 public final로 선언되어 있다. 이는 객체 필드의 참조값은 바꿀 수 없지만 해당 객체들의 필드 값은 변경 시킬 수 있기 때문에 그 필드에 관계된 불변식을 강제할 수 없다.

```java
package com.java.rule13;

public class Main13 {
    public static void main(String[] args){
        Car car = new Car();
        car.backRightTire.location="뒤왼쪽";
        car.run();
    }
}
```

반면 객체 참조 필드를 private로 선언하면 필드값을 직접적으로 접근하는 것을 막기 때문에 문제를 해결 할 수 있다.

또한 필드가 변경될 때 메소드를 통해 접근하므로 특정한 동작이 실행되도록 할 수도 있다. 

변경 불가능 객체를 참조하는 final 필드라 해도 public으로 선언하면 공개 API의 일부가 되어버리므로 내부 데이터 표현 형태를 유연하게 바꿀 수 없게 된다. -> 해당 필드를 이용하는 다른 클래스들의 호환성이 깨짐.

길이가 0 아닌 배열은 언제나 변경 가능하므로, public static final 배열 필드를 두거나, 배열 필드를 반환하는 접근자를 정의하면 안된다.

#### 위 문제에 대한 해결

1. public 으로 선언되었던 배열은 private로 바꾸고, 변경이 불가능한 public 리스틀 하나 만드는 것
2. 배열을 private로 선언하고, 해당 배열을 복사해서 반환하는 public 메서드를 하나 추가하는 것

#### 결론

* 접근 권한은 가능한 낮추라. 최소한의 public API를 설계한 다음, 다른 모든 클래스,인터페이스,멤버는 API에서 제외하라
* public static final 필드를 제외한 어떤 필드도 public 필드로 선언하지마라.
* public static final 필드가 참조하는 객체는 변경 불가능 객체로 만들어라.

# 14. public 클래스 안에는 public 필드를 두지 말고 접근자 메서드를 사용하라

``` java
class Point {
  public double x;
  public double y;
}
```

이런 클래스는 데이터 필드를 직접 조작할 수 있어서 캡슐화의 이점을 누릴 수가 없다.

#### 단점

* API 변경 불가
* 불변식 강제 불가
* 필드 사용 시 동작제어 불가

``` java
class Point {
  private double x;
  private double y;
  
  public Point(double x,double y){
    this.x = x;
    this.y = y;
  }
  public double getX() {return x;}
  public double getY() {return y;}
  
  public void setX(double x){ this.x = x;}
  public void setY(double y){ this.y = y;}
}
// 접근자 메서드와 수정자를 이용한 데이터 캡슐화
```

선언된 패키지 밖에서도 사용 가능한 클래스에는 접근자 메서드를 제공하라. public 클래스의 데이터 필드를 공개하면 변경할 때 이미 작성된 클라이언트 코드를 깨드리기 때문에 내부 표현을 변경할 수 없다.

#### default 클래스나 private 중첩 클래스는 데이터 필드를 공개하는 것이 바람 직하는 경우도 있다.

클라이언트 코드가 내부 표현에 종속된다는 문제가 있긴 하지만, 클라이언트 코드가 같은 패키지 안에 있을 수밖에 없다는 점을 고려해야 한다. Private 중첩 클래스의 경우에는, 그 클래스의 바깥 클래스 외부의 코드는 아무 영향도 받지 않을 것이다.

# 15. 변경 가능성을 최소화하라

immutable 클래스는 그 객체를 수정할 수 없는 클래스다.

객체 내부의 정보는 객체가 생성될 때 주어진 것이며, 객체가 살아 있는 동안 그대로 보존된다.

immutable 클래스는 사전의 오류 발생을 차단하기 때문에 설계, 사용 측면에서 많은 이점이 있다.

#### 변경 불가능 클래스 만드는 규칙

1. 객체 상태를 변경하는 메서드를 제공하지 않는다.

2. 상속을 불가능 하게 만든다.

3. 모든 필드를 final로 선언한다.

4. 모든 필드를 private로 선언한다.

5. 변경 가능 컴포넌트에 대한 독점적 접근권을 보장한다

   * 클라이언트가 제공하는 객체로 초기화해서는 안된다.

   * 생성자나 접근자, readObject 메서드 안에서는 방어적 복사본을 만든다.

함수형 접근법 : 피연산자를 변경하는 대신, 연산을 적용한 결과를 새롭게 만들어 반환한다.

변경 불가능 객체는 단순한다. 생성될 때 부여된 한 가지 상태만 갖는다. 따라서 생성자가 불변식을 확실히 따른다면, 해당 객체는 불변식을 절대로 어기지 않게 된다.

변경 불가능 객체는 스레드에 안전하다. 어떤 동기화도 필요 없으며, 여러 스레드가 동시에 사용해도 상태가 훼손될 일이 없다.

변경 불가능 클래스에 clone 메서드나 복사 생성자는 만들 필요도 없고, 만들어서도 안된다.

#### immutable class 단점 : 값마다 별도의 객체를 만들어야 한다

어떤 메서드도 객체를 수정해서는 안 되며, 모든 필드는 final로 선언되어야 한다

* 변경 가능한 클래스로 만들 타당한 이유가 없다면, 반드시 변경 불가능 클래스로 만들어야한다.
* 변경 불가능한 클래스로 만들 수 없다면, 변경 가능성을 최대한 제한하라.
* 특별한 이유가 없다면 모든 필드는 final로 선언하라.

``` java
final class Tire {
    private final int maxRotation;
    private final String location;

    public Tire(String location,int maxRotation){
        this.location = location;
        this.maxRotation = maxRotation;
    }

    public Tire roll(){
        if(0 < maxRotation){
            System.out.println(location+" Tire 수명: "+(maxRotation-1)+"회");
            return new Tire(this.location,this.maxRotation-1);
        }
        else{
            System.out.println("*** "+location+" Tire 펑크 ***");
            return new Tire(this.location,0);
        }
    }
}

// immutable class로 변경한 Tire class
```



# 16. 계승하는 대신 구성하라

상속은 코드 재사용을 돕는 강력한 도구지만, 항상 최선이라고는 할 수 없다. 상곡을 적절히 사용하지 못하면 깨지기 쉽다. 

* 상속은 상위 클래스와 하위 클래스 구현을 같은 프로그래머가 통제하는 단일 패키지 안에서 사용하면 안전하다.

* 상속을 위해 설계되고 그에 맞는 문서를 갖춘 클래스에 사용하는 것도 안전하다.

일반적인 객체 생성 가능 클래스라면, 해당 클래스가 속한 패키지 밖에서 상속을 시도하는 것은 위험하다.

#### 상속은 캡슐화 원칙을 위반한다.

하 클래스가 정상 동작하기 위해서는 상위 클래스의 구현에 의존할 수밖에 없다.

상위 클래스의 구현은 릴리스가 거듭되면서 바뀔 수 있는데, 그러다 보면 하위 클래스 코드는 수정된 적이 없어도 망가질 수 있다.

#### 기존 클래스를 계승하는 대신, 새로운 클래스에 기존 클래스 겍체를 참조하는 private 필드를 하나 두는 구성을 사용하자.

새로운 클래스에 포함된 각각의 메서드는 기존 클래스에 있는 메서드 가운데 필요한 것을 호출해서 그 결과를 반환하면 된다.

이런 구현 기법을 전달(forwarding)이라고 하고, 전달 기법을 사용해 구현된 메서드를 전달 메서드라고 한다.



# 19. 인터페이스는 자료형을 정의할 때만 사용하라

상수 인터페이스는 인터페이스를 잘못 사용한 것이다.
상수를 API 일부로 공개하고 싶을 때는 객체 생성이 불가능한 유틸리티 클래스에 넣어서 공개한다.
만약 이를 사용하는데 있어 클래스 이름을 붙여야해서 불편하다면 정적 임포트 기능을 사용하자.

# 20. 태그 달린 클래스 대신 클래스 계층을 활용하라

태그 달린 코드

``` java
class Figure{
 enum Shape { RECTANGLE, CIRCLE };
 
 final Shape shape;
 
 double length;
 double width;
 
 double radius;
 
 Figure(double radius) {
  shape = Shape.CIRCLE;
  this.radius = radius;
 }
 
 Figure(double length, double width){
  shape = Shape.RECTANGLE;
  this.length - length;
  this.width = width;
 }
 
 double area() {
  switch(shape) {
   case RECTANGLE:
    return length * width;
   case CIRCLE:
    return Math.PI * (radius * radius);
   default:
    throw new AssertionError();
    }
 }
}
```

태그 달린 클래스는 enum 선언, 태그 필드, switch문 등의 상투적 코드가 반복되는 클래스가 만들어지며, 서로 다른 기능을 위한 코드가 한 클래스에 모여 있으니 가독성도 떨어진다. 
또한 태그 기반 클래스에 새로운 기능을 추가하려면 소스 파일을 반드시 수정해야 한다. 수정할 때는 모든 스위치 문에 새로운 케이스를 올바르게 넣어야 한다.

# 21. 전력을 표현하고 싶을 때는 함수 객체를 사용하라

전략패턴 : C++ sort() 함수의 인자값으로 전해지는 함수포인터, 비교자 함수를 통해 정렬 전략을 표현 하는 것

자바는 함수 포인터를 지원하지 않지만 객체 참조를 통해 비슷한 효과를 달성 할 수 있다. 

```java
class StringLengthComparator{
  public int compare(String s1, String s2){
    return s1.length() - s2.length();
  }
}
```

StringLengthComparator 객체에 대한 참조는 해당 비교자에 대한 함수 포인터 구실을 한다. 따라서 이는 실행가능 전략이다.

해당 클래스는 무상태 클래스이다. 필드가 없으므로, 그 모든 객체는 기능적으로 동일하다. 따라서 싱글턴 패턴을 따르면 쓸데없는 객체 생성을 피할 수 있다.

```java
class StringLengthComparator{
  private StringLengthComparator(){}
  public static final StringLengthComparator INSTANCE = new StringLengthComparator();
}
```

#### 익명 클래스로 정의

```java
Arrays.sort(stringArray, new Comparator<String>()){
  public int compare(String s1, String s2){
    return s1.length() - s2.length();
  }
}
```

익명 클래스를 사용할 때는 sort 호출 마다 새로운 객체가 만들어지기 때문에 위 코드가 여러번 수행되는 클래스라면 함수객체를 private static final 필드에 저장하고 재사용하는 것을 고려한다.

Comparator와 같은 전략 인터페이스는 실행 가능 전략 객체들의 자료형 구실을 한다.

# 25. 배열 대신 리스트를 써라

``` java
Object [] objectArray = new Logn[1];
objectArray[0] = "I don't fit it";
```

``` java
 List<Object> ol = new ArrayList<Long>();
        ol.add("I don't fit in");
```

배열을 사용하면 프로그램 실행 중에서 오류를 발생하고 아래 처럼 리스트를 사용하면 컴파일할 때 알 수 있다.

# 30. int 상수 대신 enum을 사용하라

열거 자료형은 고정 개수의 상수들로 값이 구성되는 자료형.

``` java
public static final int APPLE_FUJI = 0;
public static final int APPLE_PIPPIN = 1;

// int를 사용한 enum 패턴
```

int enum 패턴을 사용하는 프로그램은 깨지기 쉽다. int enum 상수는 컴파일 시점 상수이기 때문에 상수를 사용하는 클라이언트 코드와 함께 컴파일 된다. 상수의 int 값이 변경되면 클라이언트도 다시 컴파일해야 한다.

```java
public enum Apple {FUJI, PIPPIN, GRANNY_SMITH }
```

열거 상수별로 하나의 객체를 public static final 필드 형태로 제공.

클라이언트가 접근 할 수 있는 생성자가 없기 때문에 final로 선언된 것이나 마찬가지이다.

1. enum 자료형의 개체수는 엄격히 통제된다.
2. enum 자료형은 컴파일 시점 형 안전성을 제공한다. Apple 형의 인자를 받는다고 선언한 메서드는 반드시 Apple 값 세게 가운데 하나만 인자로 받는다.
3. enum 자료형은 같은 이름의 상수가 평화롭게 공존 할 수 있도록한다. 즉 이름공간이 분리된다.
4. enum 자료형은 toString 메소드를 호출하면 인쇄가능 문자열로 쉽게 변환할 수 있다.

#### enum은 고정 상수 집합 역할 뿐 아니라 데이터와 연산을 가질 수도 있다.

``` java
package rule30;

public enum Planet {
    MERCURY(3.302e+23, 2.439e6),
    VENUS  (4.869e+24, 6.052e6),
    EARTH  (5.975e+24, 6.378e6),
    MARS   (6.419e+23, 3.393e6),
    JUPITER(1.899e+27, 7.149e7),
    SATURN (5.685e+26, 6.027e7),
    URANUS (8.683e+25, 2.556e7),
    NEPTUNE(1.024e+26, 2.477e7);

    private final double mass;           // 질량(단위: 킬로그램)
    private final double radius;         // 반지름(단위: 미터)
    private final double surfaceGravity; // 표면중력(단위: m / s^2)

    // 중력상수(단위: m^3 / kg s^2)
    private static final double G = 6.67300E-11;

    // 생성자
    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
        surfaceGravity = G * mass / (radius * radius);
    }

    public double mass()           { return mass; }
    public double radius()         { return radius; }
    public double surfaceGravity() { return surfaceGravity; }

    public double surfaceWeight(double mass) {
        return mass * surfaceGravity;  // F = ma
    }
}
```

Enum 상수에 데이터를 넣으려면 객체 필드를 선언하고 생성자를 통해 받은 데이터를 그 필드에 저장한다.

일반적으로 유용하게 쓰일 enum이라면, 최상위 public 클래스로 선언해야 한다.

특정한 최상위 클래스에서만 쓰이는 enum이라면 해당 클래스의 멤버 클래스로 선언해야 한다.

``` java
public enum Operation {
    PLUS,MINUS,TIMES,DIVIDE;
    
    double apply(double x, double y){
        switch (this){
            case PLUS: return x+y;
            case MINUS: return x-y;
            case TIMES: return x*y;
            case DIVIDE: return x/y;
        }
        throw new AssertionError("unknown op"+this);
    }
}
```

상수들이 각각 다르게 동작하도록 만드는 것을 구현할 경우,

위 코드는 깨지기 쉬운 코드이다. 새로운 enum 상수를 추가할 때 마다 switch 문에 case를 추가해줘야 한다.

#### 상수별 메서드 구현

```java
public enum Operation {
    PLUS("+") {
        public double apply(double x, double y) {
            return x + y;
        }
    },
    MINUS("-") {
        public double apply(double x, double y) {
            return x - y;
        }
    },
    TIMES("*") {
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE("/") {
        public double apply(double x, double y) {
            return x / y;
        }
    };

    private final String symbol;

    Operation(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }

    public abstract double apply(double x, double y);
}
```

enum 자료형에 abstract apply 메서드를 선언하고, 각 상수별 클래스 몸체 안에서 실제 메서드로 재정의한다면 새로운 상수를 추가할 때 apply 메서드 구현을 잊을 가능성이 없다. enum 자료형의 abstract 메서드는 모든 상수가 반드시 구현해야 하기 때문에 설사 잊더라도 컴파일러가 오류를 내어준다.

상수별 메서드 구현의 단점은 enum 상수끼리 공유하는 코드를 만들기가 어렵다. 이 때 switch 케이스문을 사용하면 enum에 새로운 상수를 추가할 때 유지보수 관점에서 좋지 않다.

이를 상수별 메소드 구현을 통해 만든다면, 상수마다 중복되는 코드가 들어가게 될 것인다.

#### 정책 enum 패턴

* Enum 상수를 추가할 때 마다 정책을 선택하도록 강요.
* 공유하는 작업의 메서드를 private로 선언된 중첩 enum 자료형에 넣고, 상위 enum의 생성자가 이 전략 enum 상수를 인자로 받게 한다.

``` java
enum PayrollDay {
    MONDAY(PayType.WEEKDAY), TUESDAY(PayType.WEEKDAY), WEDNESDAY(PayType.WEEKDAY),
    THURSDAY(PayType.WEEKDAY), FRIDAY(PayType.WEEKDAY),
    SATURDAY(PayType.WEEKEND), SUNDAY(PayType.WEEKEND);

    private final PayType payType;

    PayrollDay(PayType payType) { this.payType = payType; }

    int pay(int minutesWorked, int payRate) {
        return payType.pay(minutesWorked, payRate);
    }

    // 전략 열거 타입
    enum PayType {
        WEEKDAY {
            int overtimePay(int minsWorked, int payRate) {
                return minsWorked <= MINS_PER_SHIFT ? 0 :
                        (minsWorked - MINS_PER_SHIFT) * payRate / 2;
            }
        },
        WEEKEND {
            int overtimePay(int minsWorked, int payRate) {
                return minsWorked * payRate / 2;
            }
        };

        abstract int overtimePay(int mins, int payRate);
        private static final int MINS_PER_SHIFT = 8 * 60;

        int pay(int minsWorked, int payRate) {
            int basePay = minsWorked * payRate;
            return basePay + overtimePay(minsWorked, payRate);
        }
    }

    public static void main(String[] args) {
        for (PayrollDay day : values())
            System.out.printf("%-10s%d%n", day, day.pay(8 * 60, 1));
    }
}
```

외 enum 자료형 상수별로 달리 동작하는 코드를 만들 때는 enum 상수에 switch 문을 적용하면 좋다.

# 31 ordinal 대신 객체 필드를 사용하라

ordinal 메서드는 enum 자료형 안에서 enum 상수의 위치를 나타내는 정수값을 반환한다.

``` java
public enum Ensemble{
  SOLO, DUET, TRIO, QUARET, QUINTET, SEXTET, SEPTET, OCTET, NONET, DECTET;

	public int numberOfMusicians() { return ordinal()+1; }
}

```

위 코드는 유지보수 관점에서 보면 끔찍한 코드다. 상수 순러를 변경하는 순간 메서드는 깨지게된다.

#### enum 상수에 연계되는 값을 ordinal을 사용해 표현하지 마라 그런 값이 필요하다면 그 대신 객체 필드에 저장해라.

```java
public enum Ensemble {
    SOLO(1), DUET(2), TRIO(3), QUARTET(4), QUINTET(5),
    SEXTET(6), SEPTET(7), OCTET(8), DOUBLE_QUARTET(8),
    NONET(9), DECTET(10), TRIPLE_QUARTET(12);

    private final int numberOfMusicians;
    Ensemble(int size) { this.numberOfMusicians = size; }
    public int numberOfMusicians() { return numberOfMusicians; }
}
```



# 32. 비트 필드 대신 EnumSet을 사용하라

EnumSet을 사용하면 특정한 enum 자료형의 값으로 구성된 집합을 효율적으로 표현 할 수 있다.

이 클래스는 Set 인터페이스를 구현하며, Set이 제공하는 풍부한 기능들을 그대로 제공할 뿐 아니라 형 안전성, 그리고 다른 Set 구현들과 같은 수준의 상호운용성도 제공한다.

``` java
public class Text {
    public enum Style {BOLD, ITALIC, UNDERLINE, STRIKETHROUGH}

    // 어떤 Set을 넘겨도 되나, EnumSet이 가장 좋다.
    public void applyStyles(Set<Style> styles) {
        System.out.printf("Applying styles %s to text%n",
                Objects.requireNonNull(styles));
    }

    // 사용 예
    public static void main(String[] args) {
        Text text = new Text();
        text.applyStyles(EnumSet.of(Style.BOLD, Style.ITALIC));
    }
}
```

EnumSet 클래스는 비트 필드만큼 간결하고 성능도 우수할 뿐 아니라 enum 자료형의 여러가지 장점을 전부 갖추고있다.

# 35. 작명 패턴 대신 어노테이션을 사용하라

```java
// 어노테이션 자료형 선언

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//Meta 어노테이션
@Retention(RetentionPolicy.RUNTIME) // runtime에 포함되어야함.
@Target(ElementType.METHOD) // Method만을 대상으로 함
public @interface MyTest {
}
```

``` java
public class TestSample {
	@MyTest
	public static void m1() {
	}

	// annotation이 없어서 테스트 대상이 아님.
	public static void m2() {
	}
	
	@MyTest
	public static void m3() {
		throw new RuntimeException("boom boom boom");
	}

	// annotation이 없어서 테스트 대상이 아님.	
	public static void m4() {
	}
	
	@MyTest
	public void m5() {
	}
	
	public static void m6() {
	}
	
	@MyTest
	public static void m7() {
		throw new RuntimeException("crash");
	}
	
	public static void m8() {
	}
}
```

어노테이션은 sample class가 동작하는데 직접적 영향을 끼치지는 않지만 해당 어노테이션에 관심 있는 프로그램에게 유용한 정보를 제공한다. 

어노테이션은 어노테이션이 적용된 프로그램의 동작에는 절대 개입하지 않으며, 테스트 실행기와 같은 프로그램이 특별히 다루어야 하는 부분을 선언할 수 있도록 해준다.

```java
public class RunMyTests {
	public static void main(String[] args) throws Exception {
		int tests = 0;
		int passed = 0;
		Class testClass = Class.forName(args[0]);
		for (Method m : testClass.getDeclaredMethods()) {
			if (m.isAnnotationPresent(MyTest.class)) {
				tests++;
				try {
					m.invoke(null);
					passed++;
				} catch (InvocationTargetException e) {
					System.err.println(m + "failed: " + e);
				} catch (Exception e) {
					System.err.println("INVALID @MyTest: " + m);
				}
			}
		}
		
		System.out.printf("Passed: %d, Failed: %d%n",
				passed, tests - passed);
	}
}
```

이 테스트 실행기는 Test 어노테이션이 붙은 메서드를 전부 찾아 실행한다. isAnnotationPresent 메서드는 실행해야 하는 테스트 메서드를 찾는 용도로 사용된다.

# 38 인자의 유효성을 검사하라

인자의 유효성을 메소드 시작 부분에서 검사한다.

``` java
import java.util.AbstractList;
import java.util.List;

public class Exam {
    static List<Integer> intArrayAsList(final int[] a){
//        if(a == null){
//            throw new NullPointerException();
//        }

        return new AbstractList<Integer>() {
            @Override
            public Integer get(int index) {
                return a[index];
            }

            public Integer set(int i,Integer val){
                int oldVal = a[i];
                a[i] = val;
                return oldVal;
            }

            @Override
            public int size() {
                return 0;
            }
        };
    }
}
```



주석처리된 부분 때문에 배열이 null이여도 리스트 객체에 대한 참조를 반환한다. 때문에 이 시점에서 에러가 발생하지 않고 클라이언트가 해당 리스트를 사용하려는 순간 에러가 발생한다. 때문에 에러추적이 힘들고 , 디버깅이 까다로워진다.

생성자는 나중을 위해 보관될 인자의 유효성을 반드시 검사해야한다. 클래스 불변식을 위반하는 객체가 만들어지는 것을 막으려면, 생성자에 전달되는 인자의 유효성을 반드시 검사해야 한다.

하지만 이 원칙도 예외는 있다. 

#### 예외원칙

1. 유효성 검사를 실행하는 오버헤드가 너무 크거나 비현실적
2. 계산 과정에서 유효성 검사가 자연스럽게 이루어지는 경우

메서드가 생성자를 구현할 때는 받을 수 있는 인자에 제한이 있는지 따져봐야 한다. 그리고 제한이 있다면 그 사실을 문서에 남기고, 메서드 앞부분에서 검사하도록 해야 한다.



# 39 필요하다면 방어적 복사본을 만들라

#### 사용자가 클래스의 불변식을 망가뜨리기 위해 최선을 다할 것이다. 때문에 방어적으로 프로그래밍해야 한다

변경 불가능성이 보장되지 않는 변경 불가능 클래스

``` java
package com.java.rule39;

import java.util.Date;

public final class Period {
    private final Date start;
    private final Date end;

    public Period(Date start, Date end){
        if(start.compareTo(end)>0){
            throw new IllegalArgumentException(start+"after"+end);
        }
        this.start = start;
        this.end = end;
    }
    public Date start(){
        return start;
    }
    public Date end(){
        return end;
    }
}
```

``` java
package com.java.rule39;

import java.util.Date;

public class Main {
    public static void main(String[] args){
        Date start = new Date();
        Date end = new Date();
        Period period = new Period(start,end);

        end.setYear(78); // period의 불변성을 망가뜨림
        System.out.println(period.start());
        System.out.println(period.end());
    }
}
```

위 코드는 Date가 변경 가능 클래스 라는 점을 이용해서 Period의 불변성을 깨뜨리는 경우이다.

``` java
 public Period(Date start, Date end){
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());

        if(start.compareTo(this.end)>0) {
            throw new IllegalArgumentException(this.start + "after" + this.end);
        }
    }
```

위 코드는 생성자로 들어오는 인자값을 복사하여 객체의 필드로 새롭게 할당한다. 즉 변경가능한 객체의 참조를 설정하는 것이기 때문에 앞에서 사용한 공격은 먹히지 않는다.

* 인자로 전달된 객체의 자료형이 제 3자가 계승할 수 있는 자료형일 경우, 방어적 복사본을 만들 때 clone을 사용하지 않도록 해야 한다.

``` java
package com.java.rule39;

import java.util.Date;

public class Main {
    public static void main(String[] args){
        Date start = new Date();
        Date end = new Date();
        Period period = new Period(start,end);

        period.end().setYear(78);
        System.out.println(period.start());
        System.out.println(period.end());
    }
}
```

생성자 인자를 통한 공격은 막을 수 있으나 접근자를 통한 공격은 막을 수 없다.

``` java
 public Date start(){
        return new Date(start.getTime());
    }
    public Date end(){
        return new Date(end.getTime());
    }
```

변경 가능 내부 필드에 대한 방어적 복사본을 반환하도록 접근자를 수정한다.

이렇게 하면 Period 이외의 클래스가 Period 객체 내부의 변경 가능 필드에 접근 할 수 없기 때문에 객체 안에 확실히 캡슐화된 필드가 된 것이다.

제일 중요한 점은 객체의 컴포넌트로는 가능하다면 변경 불가능 객체를 사용해야 한다.

클라이언트로부터 구했거나 클라이언트에게 반환되는 변경 가능 컴포넌트가 있는 경우, 해당 클래스는 그 컴포넌트를 반드시 방의적으로 복사해야한다. 



# 40 메서드 시그니처는 신중하게 설계하라

#### 메서드 이름 신중하게 고르기

* 이해하기 쉬우면서도 같은 패키지안의 다른 이름들과 일관성이 유지되는 이름
* 좀 더 널리 합의된 사항에도 부합하는 이름
* 자바라이브러리 API이름 참고

#### 편의 메서드를 제공하는 데 너무 열올리지 마라

* 메서드는 맡은 일이 명확하고 충실해야 한다.
* 클래스에 메서드가 너무 많으면 학습,사용,테스트,유지보수 등의 모든 측면에서 어렵다.
* 클래스나 인터페이스가 수행해야 하는 동작 각각에 대해서 기능적으로 완전한 메서드를 제공하라
* 단축 메서드는 자주 쓰일 때만 추가하라

#### 인자 리스트를 길게 만들지 마라

* 4개 이하가 되도록 애쓰라.
* 자료형이 같은 인자들이 길게 연결된 인자 리스트는 특히 더 위험하다.

#### 긴 인자 리스트를 짧게 줄이기

* 여러 메소드로 나눈다
  * Java.util.List 인터페이스의 경우, 부분 리스트이 시작 첨자와 끝 첨자를 알아내는 메서드는 제공하지 않는다.
  * 대신 List 인터페이스는 subList,indexOf,lastIndexOf 메소드와 함께 사용하여 원하는 기능을 구현할 수 있다.
* Helper Class를 만들어 인자들을 그룹별로 나눈다.
* 빌더 패턴을 고쳐서 객체 생성 대신 메서드 호출에 적용한다

#### 인자의 자료형으로는 클래스보다 인터페이스가 좋다

인터페이스 대신에 클래스를 사용하면 클라이언트는 특정한 구현에 종속된다. 게다가 입력으로 이용할 데이터가 다른 형태의 객체에 보관되어 있는 경우에는 변환하고 복사하는 비용까지 짊어져야한다.

#### 인자 자료형으로 boolean을 쓰는 것보다는, 원소가 2개인 enum 자료형을 쓰는 것이 낫다.



# 41. 오버로딩할 때는 주의하라

``` java
public class CollectionClassifier {
    public static String classify(Set<?> s){
        return "Set";
    }

    public static String classify(List<?> lst){
        return "List";
    }

    public static String classify(Collection<?> c){
        return "Unknown Collection";
    }


    public static void main(String[] args){
        Collection<?>[] collections = {
                new HashSet<String>(),
                new ArrayList<BigInteger>(),
                new HashMap<String,String>().values()
        };

        for(Collection<?> c : collections){
            System.out.println(classify(c));
        }
    }
}
```

위 프로그램은 Unknown Collection을 세번 출력 한다. 

Classify 메서드가 오버로딩되어 있으며, 오버로딩된 메서드 가운데 어떤 것이 호출될지는 컴파일 시점에 결정되기 때문이다.

컴파일 시점 자료형은 전부 Collection<?>으로 동일하지만 각 인자의 실행시점 자료형이 다르다.

인자의 컴파일 시점 자료형이 Collection<?>이므로, 호출되는 것은 항상 classfy(Collection<?>) 메서드이다.

#### 오버로딩된 메서드는 정적으로 선택되지만, 오버라이딩 메소드는 동적으로 선택된다

오버로딩 메소드는 실행시점 자료형이 아무 영향도 주지 못한다. 실행될 메서드는 컴파일 시에, 인자의 컴파일 시점 자료형만을 근거로 결정된다.

위 예제는 실생시점 자료형을 근거로 오버로딩된 메소드 가운데 적절한 것을 자동으로 실행해서 인자의 자료형을 구별하려 했지만 다음과 같은 근거로 정상적으로 동작할 수 없다.

#### 오버로딩을 사용할 때는 혼란스럽지 않게 사용할 수 있도록 주의 해야한다

혼란을 피하는 전략은 같은 수의 인자를 갖는 두 개의 오버로딩 메소드를 API에 포함시키지 않는 것이다.

```java
public class SetList {
    public static void main(String[] args){
        Set<Integer> set = new TreeSet<Integer>();
        List<Integer> list = new ArrayList<Integer>();

        for(int i=-3;i<3;i++){
            set.add(i);
            list.add(i);
        }
        for(int i=0;i<3;i++){
            set.remove(i);
            list.remove(i);
        }

        System.out.println(set+" "+list);
    }
}
```

List<E> 인터페이스에 remove(E)와 remove(int)라는 오버로딩 메서드 두 개가 존재하기 때문에 발생하는 문제. 자동형변환으로 인해 list.remove(int)가 호출하여 position에 있는 값을 지운다.

형변환을 통해 인자 타입을 변경할 수 있는 자료형에 대한 오버로딩을 동시에 적용하는 것은 위험하다.

# 43 null 대신 빈 배열이나 컬렉션을 반환하라

``` java
private final List<Cheese> cheesesInStock = ...;

public Cheese[] getCheese(){
  if (cheesesInStock.size() == 0)
    return null;
}
```

위 코드는 클라이언트 입장에서  null이 반환될 때를 대비한 코드를 만들어야 한다.

```java
Cheese[] cheeses = shop.getCheeses();

if(cheese != null && Arrays.asList(cheeses).contains(Cheese.STILON))
  System.out.println("blabla");
```

```java
if(Arrays.asList(cheeses).contains(Cheese.STILON))
  System.out.println("blabla");
```

길이가 0인 배열은 immutable 하므로 아무런 제약 없이 재사용 할 수 있다.

```java
// 컬렉션에서 배열을 만들어 반환하는 올바른 방법

private final List<Cheese> cheesesInStock = ...;

private static final Cheese[] EMPTY_CHEESE_ARRAY = new Cheese[0];

public Cheese[] getCheeses(){
  return cheesesInStock.toArray(EMPTY_CHEESE_ARRAY);
}
```

toArray 메서드에 전달되는 빈 배열 상수는 반환값의 자료형을 명시하는 역할을 한다. 보통 toArray는 반환되는 원소가 담길 배열을 스스로 할당하는데, 컬렉션이 비어 있는 경우 인자로 주어진 배열을 쓴다.

```java
// 컬렉션 복사본을 반환하는 올바른 방법

public List<Cheese> getCheeseList(){
	if(cheeseInStock.isEmpty())
    return Collections.emptyList();
  else
    return new ArrayList<Cheese>(cheesesInStock);
} 
```

#### null 대신 빈 배열이나 빈 컬렉션을 반환하라.

# 45 지역 변수의 유효범위를 최소화하라

#### 지역 변수를 처음으로 사용하는 곳에서 선언하라.

사용하기 전에 선언하면 프로그램의 의도를 알고자 소스 코드를 읽는 사람만 혼란스럽다. 또한 지역 변수를 너무 빨리 선언하면 유효범위가 너무 앞쪽으로 확장될 뿐 아니라, 너무 뒤쪽으로도 확장된다.

#### 순환문을 통한 변수의 유효범위 최소화

for 문이나 for-each 문의 경우, 순환문 변수를 사용할 수 있다. 이는 순환문 내에서만 유효한 변수로 while 문보다는 for 문을 쓰는 것이 좋다.

#### 메서드의 크기를 줄이고 특정한 기능에 집중하라

두 가지 서로 다른 기능을 한 메서드 안에 넣어두면 한 가지 기능을 수행하는데 필요한 지역 변수의 유효범위가 다른 기능까지 확장되는 문제가 발생.

# 46 for 문보다는 for-each 문을 사용하라

for-each 문은 성가신 코드를 제거하고 반복자나 첨자변수를 완전히 제거해서 오류 가능성을 없앤다.

``` java
 enum Suit {CLUB,DIAMOND,HEART,SPADE}
    enum RANK {ACE,DEUCE,THREE,FOUR,FIVE,SIX,SEVEN,EIGHT,NINE,TEN,JACK,QUEEN,KING}
    
    Collection<Suit> suits = Arrays.asList(Suit.values());
    Collection<RANK> ranks = Arrays.asList(RANK.values());
    
    List<Card> deck = new ArrayList<Card>();
    for(Iterator<Suit> i = suits.iterator(); i.hasNext();) 
        for(Iterator<RANK> j = ranks.iterator(); j.hasNext();)
            deck.add(new Card(i.next(),j.next()))
```

new card 부분에서 i.next()와 j.next()가 같이 호출되므로 i가 의도한대로 호출되지 않아서 버그가 발생.

이를 for-each 문을 중첩해서 프로그램을 짜면 해결할 수있다.

```java
 for(Suit suit : suits)
        for(Rank rank: ranks)
            deck.add(new Card(suit, rank));
```

For-each 문으로는 컬렉션과 배열뿐 아니라 Iterable 인터페이스를 구현하는 어떤 객체도 순회 가능.

원소들의 그룹을 나타내는 자료형을 작성할 때는, Collection은 구현하지 않더라도 Iterable은 구현하도록 하라. 그러면 클라이언트는 for-each 문을 통해 해당 자료형을 순회할 수 있다.

#### for-each 문을 사용할 수 없는 경우

* 필터링 : 컬렉션을 순회하다 특정 원소 삭제의 경우, 이 때 반복자를 명시적으로 사용해야함
* 변환 : 리스트나 배열을 순회하다가 그 원소 가운데 일부 또는 전부의 값을 변경해야 한다면 즉 개별원소에 수정이 필요할 경우
* 병렬 순회 : 여러 컬렉션을 병렬적으로 순회해야 할 경우

# 47 어떤 라이브러리가 있는지 파악하고, 적절히 활용하라

바퀴를 다시 발명하지마라, 흔하게 쓰일법한 무언가를 개발해야 한다면, 라이브러리를 뒤져보고 있다면 그것을 사용하라

일반적으로 직접 만드는 것 보다 라이브러리에 있는 코드가 더 낫다.

# 48 정확한 답이 필요하다면 float와 double은 피하라

``` java
public static void main(String[] args){
        double funds = 1.00;
        int itemsBought = 0;
        for(double price = .10; funds >= price; price += .10){
            funds -= price;
            itemsBought++;
        }
        System.out.println(itemsBought+" items bougth.");
        System.out.println("Change : $"+funds);
    }
```

float과 double은 정확한 결과를 제공하지 않기 때문에 금전과 관련된 사항에서는 잘못된 계산이 나올 가능성이 있다.

```java
public static void main(String[] args){
        final BigDecimal TEN_CENTS = new BigDecimal(".10");
        
        int itemsBought = 0;
        BigDecimal funds = new BigDecimal("1.00");
        for(BigDecimal price = TEN_CENTS;funds.compareTo(price)>=0;price = price.add(TEN_CENTS)){
            funds = funds.subtract(price);
            itemsBought++;
        }
        System.out.println(itemsBought);
        System.out.println(funds);
    }
```

위와 같이 double 대신 BigDecimal을 사용하는 코드로 바꾸면 정확한 계산을 얻을 수 있다.

#### BigDecimal을 쓰는 방법에 대한 문제

1. 기본 산술연산 자료형보다 사용이 불편하며 느리다 -> int나 long을 사용

# 49. 객체화된 기본자료형 대신 기본 자료형을 이용하라

#### 객체화된 기본자료형 : Integer,Double,Boolean

autoboxing : 기본자료형을 객체화된 기본자료형으로 자동적으로 변환

auto-unboxing : 객체화된 기본자료형을 기본자료형으로 자동적으로 변환

이 기능들은 기본 자료형과 그 객체 표현형 간의 차이를 희미하게 만든다. 때문에 이 둘 사이에 실질적인 차이를 이해하고 어떤 상황에서 사용할 것인지 신중하게 고르는게 중요하다.

#### 객체화된 기본 자료형과 기본 자료형의 차이

1. 객체화된 기본자료형은 신원을 가진다. 따라서 객체화된 기본 자료형 객체가 두 개 있을  때, 그 값은 같더라도 신원이 다를 수 있다.

2. 기본 자료형에 저장되는 값은 전부 기능적으로 완전한 값이지만, 객체화된 기본 자료형에 저장되는 값에는 null이 들어갈 수 있다.
3. 기본 자료형은 시간이나 공간 요규량 측면에서 일반적으로 객체 표현형보다 효율적이다.

``` java
Comparator<Integer> naturalOrder = new Comparator<Integer>(){
  public int compare(Integer first, Integer second){
    return firsr < second ? -1 : (first == second ? 0: 1);
  }
}

naturalOrder.compare(new Integer(42), new Integer(42)) // 오류 발생
```

두 Integer 객체는 42라는 동일한 값을 나타내므로 이 표현식이 반환하는 값은 0이어야 한다. 하지만 실제로 반환되는 값은 1이다.

* first < second 는 Integer 객체를 기본 자료형 값으로 자동 변환.
* first의 값이  second보다 작지 않을 때 두 번째로 계산되는 표현식은 first==second이다.
* == 연산자는 객체 참조를 통해 두 객체의 신원을 비교한다. 
* 따서 f irst와  second가 다른 객체일 경우 ==는 false를 반환하므로 비교자는 1을반환한다.

#### 객체화된 기본 자료형에 == 연산자를 사용하는 것은 거의 항상 오류이다

위의 문제를 해결하는 방법 : int 변수에 auto-unboxing을 통해 기본자료형으로 비교.

``` java
Comparator<Integer> naturalOrder = new Comparator<Integer>(){
  public int compare(Integer first, Integer second){
    int f = first; //auto-unboxing
    int s = second;
    return f < s ? -1 : (f == s ? 0: 1);
  }
}
```

``` Java
public class Unbelevable{
  static Integer i;
  
  public static void main(String[] args){
    if(i == 42)
      System.out.println("언블리버블");
  }
}
```

위 코드는 i==42를 계산할 때 NullPointerException을 발생시킨다. 

기본 자료형과 객체화된 기본 자료형을 한 연산 안에 엮어 놓으면 객체화된 기본 자료형은 자동으로 기본 자료형으로 변환된다. 따라서 null 객체 참조를 기본 자료형으로 변환하려 시도하여 NullPountException이 발생한다.

# 50 다른 자료형이 적절하다면 문자열 사용은 피하라

#### 문자열은 값 자료형을 대신하기에는 부족하다

데이터가 네트워크나 키보드를 통해서 들어올 때는 보통 문자열 형태로인데 그 때 그대로 두려는 것은 좋지 않다. 데이터가 원래 텍스트 형태일 때나 그렇게하고 숫자라면 기본 자료형으로 변환해서 사용한다.

#### 문자열은  enum 자로형을 대신하기에는 부족하다

enum은 문자열보다 훨씬 좋은 열거 자료형 상수들을 만들어 낸다.

#### 문자열은 혼한 자료형을 대신하기엔 부족하다

```java
String compoundKey = className + "#" +i.next();
```

1. 필드 구분자로 사용한 문자가 필드 안에 들어가버리면 문제가 발생.
2. 각 필드를 사용하려면 문자열을 파싱해야 하는 오버헤드 발생
3. Equals, toString, compareTo 메서드 같은 것을 제공할 수 없고 String이 제공하는 기능들만 이용해야함.
4. 혼 자료형을 표현할 클래스를 만드는 편이 낫다

#### 문자열은 권한을 표현하기엔 부족하다

``` java
// 클라이언트가 제공한 문자열 키로 스레드 지역 변수를 식별하도록 하는 설계
public calss ThreadLocal{
  private ThreadLocal(){}
  
  public static void set(String key, Object value);
  
  public static Object get(String Key);
}
```

문자열이 스레드 지역 변수의 전역적인 namespace이다. 위 접근법이 통하려면 클라이언트가 제공하는 문자열의 키 유일성이 보장되어야 한다. 또한 악의적인 클라이언트가 같은 문자열 접근을 통해 다른 클라이언트의 데이터에 접글할 수 있게 된다.

# 51. 문자열 연결 시 성능에 주의하라

String은 immutable 객체이기 때문에 + 연산시 새로 객체를 생성해서 반환한다. 따라서 + 연산이 많이 사용될 때는 String 대신 StringBuilder를 사용하는 것이 낫다.

# 52. 객체를 참조할 때는 그 인터페이스를 사용하라

#### 적당한 인터페이스 자료형이 있다면 인자나 반환값, 변수, 필드의 자료형은 클래스 대신 인터페이스로 선언하자

인터페이스를 자료형으로 사용하면 프로그램은 더욱 유연해진다.

# 55. 신중하게 최적화 하라

* 빠른 프로그램이 아닌, 좋은 프로그램을 만들려 노력하라
* 설계를 할 때는 성능을 제약할 가능성이 있는 결정들은 피하라
* 잘 설계된 API는 일반적으로 좋은 성능을 보인다

빠른 프로그램을 만들고자 애쓰는 것 보다 좋은 프로그램을 짜기 위해 노력하면 성능은 따라올 것이다. 하지만 모든 문제에 이런 관점을 적용시키기 보다는 시스템의 특성을 잘 파악하고 사고를 유연하게 가질 필요가 있다. 

# 56. 일반적으로 통용되는 작명 관습을 따르라

* 패키지 이름은 마침표를 구분점으로 사용하는 계층적 이름
  * 알파벳 소문자, 숫자는 거의 사용하지 않음
  * 최상위 도메인 이름이 먼저온다.
  * 패키지명 컴포넌트는 짧아야 하며, 보통 여덟 문자 이하
  * 패키지명 상당수는 도메인 이름 외 단 하나의 컴포넌트만 사용하고 여러 개의 정보 계층으로 나눠야할 큰 기능이라면 추가 컴포넌트를 사용한다.
* enum이나 어노테이션 자료형 이름을 비롯, 클래스나 인터페이스 이름은 하나 이상의 단어로 구성
  * 각 단어의 첫 글자는 대문자
  * cameCase 적용
* 메서드나 필드 이름은 클래스나 인터페이스 이름과 동일한 철자 규칙을 따른다
  * 첫 글자는 소문자
  * 상수 필드는 하나이상의 대문자로 구성되며 단어사이는 밑줄을 통해 구분한다.
* 메드는 일반적으로 동사나 동사구를 이름으로 사용
  * boolean 값을 반환하는 메서드의 이름은 보통 is, has
  * 객체 속성을 반환하는 메서드는 명사나 명사구 또는 보통 get으로 시작하는 동사구를 이름으로 붙인다.
* 빈 클래스에 속한 메서드의 이름은 반드시 g et으로 시작해야 한다
* 객체의 자료형을 변환하는 메서드, 다른 자료형의 독립적 객체를 반환하는 메서드에는 보통  toType 형태의 이름을 붙인다.
* 인로 전달받은 객체와 다른 자료형의 view 객체를 반환하는 메서드에는  asType 형태의 이름을 붙인다.
* boolean 형의 필드에는 보통 boolean 메서드와 같은 이름을 붙이나, 접두어  is는 생략한다.

