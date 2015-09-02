package mapper;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by yoon on 15. 9. 2..
 */
public class ConstraintTypeTest {

    @Test
    public void 캐스팅가능여부_함수_테스트() {
        ConstraintType type = ConstraintType.Type_Integer;
        assertThat(type.isValidRequest("123"), is(true));
        assertThat(type.isValidRequest("-123"), is(true));

        assertThat(type.isValidRequest("123.0"), is(false));
        assertThat(type.isValidRequest("test"), is(false));
    }
}