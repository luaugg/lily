defmodule Lily.Commands.List do
  import Lily.Commands.Server, only: [add_command: 2]
  import Nostrum.Api, only: [create_message: 2]

  defp ping(message, _args), do:
    create_message(message.channel_id, "Pong!")

  # Child spec stuff below

  def child_spec(_init \\ :ok) do
    %{
      id: __MODULE__,
      start: {__MODULE__, :start, []}
    }
  end

  def start do
    :ok = add_command("ping", &ping/2)
    :ignore
  end
end
