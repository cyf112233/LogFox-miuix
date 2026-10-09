/*
 * Pre-generated equivalent of `src/main/aidl/.../IUserService.aidl`.
 *
 * The `.aidl` file is still the source of truth, but this interface is committed already generated
 * because the Android SDK ships `aidl` as a native (static) executable which crashes with SIGILL on
 * some aarch64 / PRoot environments, making `compileDebugAidl` impossible to run there.
 * `buildFeatures.aidl` is therefore disabled for this module.
 *
 * To go back to on-the-fly generation: delete this file and set `buildFeatures.aidl = true`.
 */
package com.f0x1d.logfox.feature.terminals.presentation;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import com.f0x1d.logfox.feature.terminals.api.model.TerminalResult;

public interface IUserService extends IInterface {
    void destroy() throws RemoteException;

    void exit() throws RemoteException;

    TerminalResult executeNow(String command) throws RemoteException;

    long execute(String command) throws RemoteException;

    ParcelFileDescriptor processOutput(long processId) throws RemoteException;

    ParcelFileDescriptor processError(long processId) throws RemoteException;

    ParcelFileDescriptor processInput(long processId) throws RemoteException;

    void destroyProcess(long processId) throws RemoteException;

    abstract class Stub extends Binder implements IUserService {
        public static final String DESCRIPTOR =
                "com.f0x1d.logfox.feature.terminals.presentation.IUserService";

        static final int TRANSACTION_destroy = 16777114;
        static final int TRANSACTION_exit = 1;
        static final int TRANSACTION_executeNow = 2;
        static final int TRANSACTION_execute = 3;
        static final int TRANSACTION_processOutput = 4;
        static final int TRANSACTION_processError = 5;
        static final int TRANSACTION_processInput = 6;
        static final int TRANSACTION_destroyProcess = 7;

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        public static IUserService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin instanceof IUserService) {
                return (IUserService) iin;
            }
            return new Proxy(obj);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                throws RemoteException {
            String descriptor = DESCRIPTOR;
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(descriptor);
                    return true;
                }
                case TRANSACTION_destroy: {
                    data.enforceInterface(descriptor);
                    this.destroy();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_exit: {
                    data.enforceInterface(descriptor);
                    this.exit();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_executeNow: {
                    data.enforceInterface(descriptor);
                    String _arg0 = data.readString();
                    TerminalResult _result = this.executeNow(_arg0);
                    reply.writeNoException();
                    writeNullable(reply, _result);
                    return true;
                }
                case TRANSACTION_execute: {
                    data.enforceInterface(descriptor);
                    String _arg0 = data.readString();
                    long _result = this.execute(_arg0);
                    reply.writeNoException();
                    reply.writeLong(_result);
                    return true;
                }
                case TRANSACTION_processOutput: {
                    data.enforceInterface(descriptor);
                    long _arg0 = data.readLong();
                    ParcelFileDescriptor _result = this.processOutput(_arg0);
                    reply.writeNoException();
                    writeNullable(reply, _result);
                    return true;
                }
                case TRANSACTION_processError: {
                    data.enforceInterface(descriptor);
                    long _arg0 = data.readLong();
                    ParcelFileDescriptor _result = this.processError(_arg0);
                    reply.writeNoException();
                    writeNullable(reply, _result);
                    return true;
                }
                case TRANSACTION_processInput: {
                    data.enforceInterface(descriptor);
                    long _arg0 = data.readLong();
                    ParcelFileDescriptor _result = this.processInput(_arg0);
                    reply.writeNoException();
                    writeNullable(reply, _result);
                    return true;
                }
                case TRANSACTION_destroyProcess: {
                    data.enforceInterface(descriptor);
                    long _arg0 = data.readLong();
                    this.destroyProcess(_arg0);
                    reply.writeNoException();
                    return true;
                }
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        private static void writeNullable(Parcel reply, android.os.Parcelable value) {
            if (value != null) {
                reply.writeInt(1);
                value.writeToParcel(reply, 0);
            } else {
                reply.writeInt(0);
            }
        }

        private static class Proxy implements IUserService {
            private final IBinder mRemote;

            Proxy(IBinder remote) {
                mRemote = remote;
            }

            @Override
            public IBinder asBinder() {
                return mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public void destroy() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(TRANSACTION_destroy, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void exit() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(TRANSACTION_exit, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public TerminalResult executeNow(String command) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                TerminalResult _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(command);
                    mRemote.transact(TRANSACTION_executeNow, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = TerminalResult.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public long execute(String command) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                long _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(command);
                    mRemote.transact(TRANSACTION_execute, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readLong();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public ParcelFileDescriptor processOutput(long processId) throws RemoteException {
                return readPipe(TRANSACTION_processOutput, processId);
            }

            @Override
            public ParcelFileDescriptor processError(long processId) throws RemoteException {
                return readPipe(TRANSACTION_processError, processId);
            }

            @Override
            public ParcelFileDescriptor processInput(long processId) throws RemoteException {
                return readPipe(TRANSACTION_processInput, processId);
            }

            private ParcelFileDescriptor readPipe(int code, long processId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                ParcelFileDescriptor _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeLong(processId);
                    mRemote.transact(code, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = ParcelFileDescriptor.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public void destroyProcess(long processId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeLong(processId);
                    mRemote.transact(TRANSACTION_destroyProcess, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
