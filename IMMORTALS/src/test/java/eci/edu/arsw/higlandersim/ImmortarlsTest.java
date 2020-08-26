package eci.edu.arsw.higlandersim;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import edu.eci.arsw.highlandersim.Immortal;

public class ImmortarlsTest {
	private List<Immortal> immortals;
	private static final int DEFAULT_IMMORTAL_HEALTH = 100;
	private static final int DEFAULT_DAMAGE_VALUE = 10;
	
	@Test
	public void deberiaMantenerElInvarianteCon10() throws InterruptedException {
		immortals = setupInmortals(10);
        if (immortals != null) {
            for (Immortal im : immortals) {
                im.start();
            }
        }
        Thread.sleep(500);
        int res = pause();
        resume();
        assertTrue(res==(10*100));
	}
	@Test
	public void deberiaMantenerElInvarianteCon100() throws InterruptedException {
		immortals = setupInmortals(100);
        if (immortals != null) {
            for (Immortal im : immortals) {
                im.start();
            }
        }
        Thread.sleep(500);
        int res = pause();
        resume();
        assertTrue(res==(100*100));
	}
	
	public int pause() {
        int sum = 0;
        for (Immortal im : immortals) {
            sum += im.getHealth();
            im.pause();
        }
        return sum;
	}
	
	public void resume() {
		for (Immortal im : immortals) {
            im.resumen();
        }
	}
	
	public List<Immortal> setupInmortals(int numberImmortals) {       
        try {
            int ni = numberImmortals;

            List<Immortal> il = new LinkedList<Immortal>();

            for (int i = 0; i < ni; i++) {
                Immortal i1 = new Immortal("im" + i, il, DEFAULT_IMMORTAL_HEALTH, DEFAULT_DAMAGE_VALUE,null);
                il.add(i1);
            }
            return il;
        } catch (NumberFormatException e) {
            fail();
            return null;
        }

    }

}
