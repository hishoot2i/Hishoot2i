package com.manifest.data;

import com.manifest.stream.LittleEndianStreamer;
import com.manifest.stream.MfStreamer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MfFile {

    public final List<StartTagChunk> startTagChunks;
    public final List<EndTagChunk> endTagChunks;
    public final List<TagChunk> tagChunks;
    // private static final String TAG = MfFile.class.getSimpleName();
    public MfHeader header;
    public StringChunk stringChunk;
    @SuppressWarnings({"FieldCanBeLocal", "unused", "RedundantSuppression"})
    private ResourceIdChunk resourceIdChunk;
    @SuppressWarnings({"FieldCanBeLocal", "unused", "RedundantSuppression"})
    private StartNamespaceChunk startNamespaceChunk;
    @SuppressWarnings({"FieldCanBeLocal", "unused", "RedundantSuppression"})
    private EndNamespaceChunk endNamespaceChunk;
    private MfStreamer mStreamer;

    public MfFile() {
        startTagChunks = new ArrayList<>();
        endTagChunks = new ArrayList<>();
        tagChunks = new ArrayList<>();
    }

    public void parse(InputStream inputStream) throws IOException {
        mStreamer = new LittleEndianStreamer();
        byte[] headerBytes = new byte[MfHeader.LENGTH];
        //noinspection ResultOfMethodCallIgnored
        inputStream.read(headerBytes, 0, headerBytes.length);
        header = parseHeader(headerBytes);

        int chunkIdx = 0;
        int cursor = MfHeader.LENGTH;
        do {
            byte[] infoBytes = new byte[ChunkInfo.LENGTH];
            cursor += inputStream.read(infoBytes, 0, infoBytes.length);
            mStreamer.use(infoBytes);
            ChunkInfo info = ChunkInfo.parseFrom(mStreamer);
            info.chunkIndex = chunkIdx++;

            // Chunk size = ChunkInfo + BodySize
            byte[] chunkBytes = new byte[(int) info.chunkSize];
            System.arraycopy(infoBytes, 0, chunkBytes, 0, ChunkInfo.LENGTH);
            cursor += inputStream.read(chunkBytes, ChunkInfo.LENGTH, (int) info.chunkSize - ChunkInfo.LENGTH);
            StartTagChunk startTagChunk;
            EndTagChunk endTagChunk;
            switch ((int) info.chunkType) {
                case ChunkInfo.STRING_CHUNK_ID:
                    stringChunk = parseStringChunk(chunkBytes);
                    break;
                case ChunkInfo.RESOURCE_ID_CHUNK_ID:
                    resourceIdChunk = parseResourceIdChunk(chunkBytes);
                    break;
                case ChunkInfo.START_NAMESPACE_CHUNK_ID:
                    startNamespaceChunk = parseStartNamespaceChunk(chunkBytes);
                    break;
                case ChunkInfo.START_TAG_CHUNK_ID:
                    startTagChunk = parseStartTagChunk(chunkBytes);
                    startTagChunks.add(startTagChunk);
                    tagChunks.add(startTagChunk);
                    break;
                case ChunkInfo.EDN_TAG_CHUNK_ID:
                    endTagChunk = parseEndTagChunk(chunkBytes);
                    endTagChunks.add(endTagChunk);
                    tagChunks.add(endTagChunk);
                    break;
                case ChunkInfo.CHUNK_END_NS_CHUNK_ID:
                    endNamespaceChunk = parseEndNamespaceChunk(chunkBytes);
                    break;
                default:
                    break;
            }
            // LogUtil.i(TAG, info.toString());
        } while (cursor < header.fileLength);
    }

//    public void parse(File file) throws IOException {
//        try (FileInputStream fileInputStream = new FileInputStream(file)) {
//            parse(fileInputStream);
//        }
//    }
//    public void parse(RandomAccessFile racFile) throws IOException {
//        racFile.seek(0);
//        try (FileInputStream fileInputStream = new FileInputStream(racFile.getFD())) {
//            parse(fileInputStream);
//        }
//    }
//    public String raw() {
//        StringBuilder builder = new StringBuilder(4096);
//        builder.append('\n');
//        builder.append(header).append('\n');
//        builder.append(stringChunk).append('\n');
//        builder.append(resourceIdChunk).append('\n');
//        builder.append(startNamespaceChunk).append('\n');
//
//        builder.append("-- StartTag Chunks --").append('\n');
//        for (int i = 0; i < startTagChunks.size(); ++i) {
//            builder.append("StartTagChunk_").append(i).append('\n');
//            builder.append(startTagChunks.get(i)).append('\n');
//        }
//        builder.append("-- EndTag Chunks --").append('\n');
//        for (int i = 0; i < endTagChunks.size(); ++i) {
//            builder.append("EndTagChunk_").append(i).append('\n');
//            builder.append(endTagChunks.get(i)).append('\n');
//        }
//        builder.append(endNamespaceChunk).append('\n');
//        return builder.toString();
//    }
//    public String xml() {
//        StringBuilder builder = new StringBuilder(4096);
//        int depth = 0;
//        builder.append("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>\n");
//        for (TagChunk tagChunk : tagChunks) {
//            if (tagChunk instanceof StartTagChunk) {
//                builder.append(createStartTagXml((StartTagChunk) tagChunk, depth));
//                ++depth;
//            } else if (tagChunk instanceof EndTagChunk) {
//                --depth;
//                builder.append(createEndTagXml((EndTagChunk) tagChunk, depth));
//            }
//        }
//        return builder.toString();
//    }
//    private String createStartTagXml(StartTagChunk chunk, int depth) {
//        StringBuilder builder = new StringBuilder(256);
//        String lineIndent = makeLineIndent(depth, 4);
//        if ("manifest".equals(chunk.nameStr)) {
//            builder.append("<manifest\n");
//            for (Map.Entry<String, String> entry : startNamespaceChunk.prefix2UriMap.entrySet()) {
//                builder.append("    ").append("xmlns:").append(entry.getKey()).append("=")
//                        .append("\"").append(entry.getValue()).append("\"");
//            }
//        } else {
//            builder.append(lineIndent);
//            builder.append("<").append(chunk.nameStr);
//        }
//        List<AttributeEntry> attrEntries = chunk.attributes;
//        if (attrEntries.size() > 0) {
//            for (AttributeEntry entry : attrEntries) {
//                builder.append("\n");
//                builder.append(lineIndent).append("    ");
//                String prefixName = startNamespaceChunk.uri2prefixMap.get(entry.namespaceUriStr);
//                if (prefixName != null) {
//                    builder.append(prefixName).append(':');
//                }
//                builder.append(entry.nameStr).append('=')
//                        .append("\"").append(entry.dataStr).append("\"");
//            }
//        }
//        builder.append(" >\n");
//        return builder.toString();
//    }
//    private String createEndTagXml(EndTagChunk chunk, int depth) {
//        StringBuilder builder = new StringBuilder(256);
//        String lineIndent = makeLineIndent(depth, 4);
//        builder.append(lineIndent);
//        builder.append("</").append(chunk.nameStr).append(">").append('\n');
//        return builder.toString();
//    }

//    private String makeLineIndent(int depth, @SuppressWarnings("SameParameterValue") int indent) {
//        return " ".repeat(Math.max(0, depth * indent));
//    }

    private MfHeader parseHeader(byte[] data) {
        mStreamer.use(data);
        return MfHeader.parseFrom(mStreamer);
    }

    private StringChunk parseStringChunk(byte[] chunkData) {
        mStreamer.use(chunkData);
        return StringChunk.parseFrom(mStreamer);
    }

    private ResourceIdChunk parseResourceIdChunk(byte[] chunkData) {
        mStreamer.use(chunkData);
        return ResourceIdChunk.parseFrom(mStreamer);
    }

    private StartNamespaceChunk parseStartNamespaceChunk(byte[] chunkData) {
        mStreamer.use(chunkData);
        return StartNamespaceChunk.parseFrom(mStreamer, stringChunk);
    }

    private StartTagChunk parseStartTagChunk(byte[] chunkData) {
        mStreamer.use(chunkData);
        return StartTagChunk.parseFrom(mStreamer, stringChunk);
    }

    private EndTagChunk parseEndTagChunk(byte[] chunkData) {
        mStreamer.use(chunkData);
        return EndTagChunk.parseFrom(mStreamer, stringChunk);
    }

    private EndNamespaceChunk parseEndNamespaceChunk(byte[] chunkData) {
        mStreamer.use(chunkData);
        return EndNamespaceChunk.parseFrom(mStreamer, stringChunk);
    }
}
