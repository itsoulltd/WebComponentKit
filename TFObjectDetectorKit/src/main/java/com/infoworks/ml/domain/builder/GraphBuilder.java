package com.infoworks.ml.domain.builder;

import org.tensorflow.DataType;
import org.tensorflow.Graph;
import org.tensorflow.Output;
import org.tensorflow.Tensor;

public class GraphBuilder {

    public GraphBuilder(Graph g) {
        this.g = g;
    }

    public Output div(Output x, Output y) {
        return binaryOp("Div", x, y);
    }

    public Output sub(Output x, Output y) {
        return binaryOp("Sub", x, y);
    }

    public Output resizeBilinear(Output images, Output size) {
        return binaryOp("ResizeBilinear", images, size);
    }

    public Output expandDims(Output input, Output dim) {
        return binaryOp("ExpandDims", input, dim);
    }

    public Output cast(Output value, DataType dtype) {
        return g.opBuilder("Cast", "Cast").addInput(value).setAttr("DstT", dtype).build().output(0);
    }

    public Output decodeJpeg(Output contents, long channels) {
        return g.opBuilder("DecodeJpeg", "DecodeJpeg")
                .addInput(contents)
                .setAttr("channels", channels)
                .build()
                .output(0);
    }

    public Output constant(String name, Object value) {
        try (Tensor t = Tensor.create(value)) {
            return g.opBuilder("Const", name)
                    .setAttr("dtype", t.dataType())
                    .setAttr("value", t)
                    .build()
                    .output(0);
        }
    }

    private Output binaryOp(String type, Output in1, Output in2) {
        return g.opBuilder(type, type).addInput(in1).addInput(in2).build().output(0);
    }

    private Graph g;
}
