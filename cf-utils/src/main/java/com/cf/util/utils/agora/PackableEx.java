package com.cf.util.utils.agora;

public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}
