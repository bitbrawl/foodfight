package org.bitbrawl.foodfight.engine.video;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.humble.video.Codec;
import io.humble.video.Encoder;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.Muxer;
import io.humble.video.MuxerFormat;
import io.humble.video.PixelFormat;
import io.humble.video.Rational;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;

public final class ImageEncoder implements AutoCloseable {

	private final Path videoFile;
	private final Path unfinished;
	private int frameNumber;

	private final Muxer muxer;
	private final Encoder encoder;
	private MediaPictureConverter converter;
	private final MediaPicture picture;
	private final MediaPacket packet;

	public ImageEncoder(Path videoFile) throws InterruptedException, IOException {

		this.videoFile = videoFile;
		unfinished = videoFile.resolveSibling(videoFile.getFileName().toString().replace(".mp4", ".tmp"));

		MuxerFormat format = MuxerFormat.guessFormat("mp4", videoFile.getFileName().toString(), "video/mp4");
		muxer = Muxer.make(unfinished.toString(), format, null);

		format = muxer.getFormat();
		Codec codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());
		encoder = Encoder.make(codec);
		encoder.setWidth(FrameGenerator.FRAME_WIDTH);
		encoder.setHeight(FrameGenerator.FRAME_HEIGHT);
		PixelFormat.Type pixelFormat = PixelFormat.Type.PIX_FMT_YUV420P;
		encoder.setPixelFormat(pixelFormat);
		Rational framerate = Rational.make(1, FRAMERATE);
		encoder.setTimeBase(framerate);
		if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER))
			encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);
		encoder.open(null, null);
		muxer.addNewStream(encoder);
		muxer.open(null, null);
		converter = null;
		picture = MediaPicture.make(encoder.getWidth(), encoder.getHeight(), pixelFormat);
		picture.setTimeBase(framerate);
		packet = MediaPacket.make();

	}

	public void encode(BufferedImage image) {

		if (converter == null)
			converter = MediaPictureConverterFactory.createConverter(image, picture);
		converter.toPicture(picture, image, frameNumber++);

		do {
			encoder.encode(packet, picture);
			if (packet.isComplete())
				muxer.write(packet, false);
		} while (packet.isComplete());

	}

	@Override
	public void close() throws IOException {

		do {
			encoder.encode(packet, null);
			if (packet.isComplete())
				muxer.write(packet, false);
		} while (packet.isComplete());

		muxer.close();

		Files.move(unfinished, videoFile);

	}

	private static final int FRAMERATE = 30;

}
