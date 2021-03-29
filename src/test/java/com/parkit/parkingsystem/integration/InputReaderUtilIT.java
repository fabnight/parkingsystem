package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class InputReaderUtilIT {

	private static InputReaderUtil inputReaderUtil;
	private static InputStream inputStream;
	private static Scanner scan;

	@BeforeEach
	private void setUpPerTest() throws Exception {
		// when(inputReaderUtil.readSelection()).thenReturn(3);
		inputReaderUtil = new InputReaderUtil();
	}

	@AfterEach
	public void tearDown() {
	}

	@Test
	public void testInputReaderUtil() {
		String input = "1";

		inputReaderUtil.readSelection();
		inputStream = new ByteArrayInputStream((input).getBytes(Charset.forName("UTF-8")));
		inputReaderUtil.readSelection();
		scan = new Scanner(inputStream);

		assertEquals("1", inputReaderUtil.readSelection());
	}

}
