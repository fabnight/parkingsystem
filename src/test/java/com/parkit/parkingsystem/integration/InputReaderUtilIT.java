package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class InputReaderUtilIT {
	private Scanner scan = new Scanner((System.in), "UTF-8");

	private void setScan(final Scanner scanner) {
		scan = scanner;
	}

	private InputReaderUtil inputReaderUtil;

	@BeforeEach
	private void setUpPerTest() throws Exception {
		// when(inputReaderUtil.readSelection()).thenReturn(3);
		inputReaderUtil = new InputReaderUtil();
	}

	@AfterEach
	public void tearDown() {
	}

	@Tag("ReadSelection")
	@Test
	public void testInputReaderUtil() {

		scan = new Scanner(System.in);
		scan.next("1");
		int resultat = inputReaderUtil.readSelection();

		setScan(scan);

		assertEquals(1, resultat);
		scan.close();

	}

}
