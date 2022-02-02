package fr.redboxing.incarnam.utils.serialization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.*;

public abstract class BinarSerialPart
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BinarSerialPart.class);
    private static final int DEFAULT_PART_SIZE_IN_BYTES = 32;
    private BinarSerialDataSource m_dataSource;
    protected boolean m_error;
    public static final BinarSerialPart EMPTY;
    private final int m_expectedSize;

    public BinarSerialPart() {
        super();
        this.m_error = false;
        this.m_expectedSize = 32;
    }

    public BinarSerialPart(final int expectedSize) {
        super();
        this.m_error = false;
        this.m_expectedSize = expectedSize;
    }

    public BinarSerialDataSource getDataSource() {
        return this.m_dataSource;
    }

    public void setDataSource(final BinarSerialDataSource dataSource) {
        this.m_dataSource = dataSource;
    }

    protected void clear() {
        this.m_error = false;
    }

    public void grabDataFromSource() {
        if (this.m_dataSource != null) {
            this.m_dataSource.updateToSerializedPart();
        }
    }

    public void notifyListener() {
        if (this.m_dataSource != null) {
            this.m_dataSource.onDataChanged();
        }
    }

    protected void markAsSuccess() {
        this.m_error = false;
    }

    protected void markAsError(final String errorMsg) {
        LOGGER.error(errorMsg);
        this.m_error = true;
    }

    protected void markAsError(final String errorMsg, final Exception e) {
        LOGGER.error(errorMsg, e);
        this.m_error = true;
    }

    public boolean hasError() {
        return this.m_error;
    }

    void clearError() {
        this.m_error = false;
    }

    public int expectedSize() {
        return this.m_expectedSize;
    }

    public abstract void serialize(final ByteBuffer p0);

    public abstract void unserialize(final ByteBuffer p0, final int p1);

    static {
        EMPTY = new BinarSerialPart() {
            @Override
            public void setDataSource(final BinarSerialDataSource dataSource) {
                throw new UnsupportedOperationException("Impossible de d\u00e9finir un datasource pour la part EMPTY");
            }

            @Override
            public void serialize(final ByteBuffer buffer) {
            }

            @Override
            public void unserialize(final ByteBuffer buffer, final int version) {
            }
        };
    }
}
